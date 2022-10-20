package com.coolbeevip.debezium.binlog;

import com.coolbeevip.debezium.message.DmlMessage;
import io.debezium.config.Configuration;
import io.debezium.data.Envelope.Operation;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.debezium.data.Envelope.FieldName.AFTER;
import static io.debezium.data.Envelope.FieldName.BEFORE;
import static io.debezium.data.Envelope.FieldName.OPERATION;
import static io.debezium.data.Envelope.FieldName.SOURCE;
import static java.util.stream.Collectors.toMap;

public class DebeziumBinlogListener {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final Executor executor = Executors.newSingleThreadExecutor();

  private DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;

  private List<String> filterTables;

  public DebeziumBinlogListener(Configuration customerConnectorConfiguration, List<String> filterTables) {

    this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
        .using(customerConnectorConfiguration.asProperties())
        .notifying(this::handleChangeEvent)
        .build();
    this.filterTables = filterTables;
  }

  private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
    SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
    Struct sourceRecordChangeKey = (Struct) sourceRecord.key();
    Struct sourceRecordChangeValue = (Struct) sourceRecord.value();
    if (sourceRecordChangeValue != null) {
      Operation operation = Operation.forCode((String) sourceRecordChangeValue.get(OPERATION));
      if (operation == Operation.READ) {
        // 忽略读取操作
      } else if (operation == Operation.CREATE) {
        dmlMessage(operation, sourceRecordChangeKey, sourceRecordChangeValue);
      } else if (operation == Operation.UPDATE) {
        dmlMessage(operation, sourceRecordChangeKey, sourceRecordChangeValue);
      } else if (operation == Operation.DELETE) {
        dmlMessage(operation, sourceRecordChangeKey, sourceRecordChangeValue);
      } else if (operation == Operation.TRUNCATE) {
        dmlMessage(operation, sourceRecordChangeKey, sourceRecordChangeValue);
      }
    }
  }

  private void dmlMessage(Operation operation, Struct sourceRecordChangeKey,
      Struct sourceRecordChangeValue) {

    Struct sourceStruct = (Struct) sourceRecordChangeValue.get(SOURCE);
    String dbName = sourceStruct.get("db").toString();
    String tableName = sourceStruct.get("table").toString();
    Long ts = (Long) sourceStruct.get("ts_ms");

    if (this.filterTables.contains(tableName)) {
      DmlMessage dmlMessage = new DmlMessage();
      dmlMessage.setDbName(dbName);
      dmlMessage.setTable(tableName);
      dmlMessage.setOperation(operation.name());
      dmlMessage.setTimestamp(ts);

      // key
      Map<String, Object> key = sourceRecordChangeKey.schema().fields().stream()
          .map(Field::name)
          .filter(fieldName -> sourceRecordChangeKey.get(fieldName) != null)
          .map(fieldName -> Pair.of(fieldName, sourceRecordChangeKey.get(fieldName)))
          .collect(toMap(Pair::getKey, Pair::getValue));
      dmlMessage.setKey(key);

      if (operation == Operation.DELETE) {
        dmlMessage.setPayload(payload(sourceRecordChangeValue, BEFORE));
      } else if (operation == Operation.CREATE) {
        dmlMessage.setPayload(payload(sourceRecordChangeValue, AFTER));
      } else if (operation == Operation.UPDATE) {
        Struct valueBeforeStruct = (Struct) sourceRecordChangeValue.get(BEFORE);
        Struct valueAfterStruct = (Struct) sourceRecordChangeValue.get(AFTER);
        Map<String, Pair<Object, Object>> payloadChanged = valueAfterStruct.schema().fields()
            .stream()
            .map(Field::name)
            .filter(fieldName -> {
              return !valueAfterStruct.get(fieldName).equals(valueBeforeStruct.get(fieldName));
            })
            .map(fieldName -> Pair.of(fieldName,
                Pair.of(valueBeforeStruct.get(fieldName), valueAfterStruct.get(fieldName))))
            .collect(toMap(Pair::getKey, Pair::getValue));
        dmlMessage.setPayloadChanged(payloadChanged);
        dmlMessage.setPayload(payload(sourceRecordChangeValue, AFTER));
      }

      log.info(dmlMessage.toJSON());
    }
  }

  private Map<String, Object> payload(Struct sourceRecordChangeValue, String record) {
    Struct valueStruct = (Struct) sourceRecordChangeValue.get(record);
    Map<String, Object> payload = valueStruct.schema().fields().stream()
        .map(Field::name)
        .filter(fieldName -> valueStruct.get(fieldName) != null)
        .map(fieldName -> Pair.of(fieldName, valueStruct.get(fieldName)))
        .collect(toMap(Pair::getKey, Pair::getValue));
    return payload;
  }

  public void start() {
    this.executor.execute(debeziumEngine);
  }

  public void stop() throws IOException {
    if (this.debeziumEngine != null) {
      this.debeziumEngine.close();
    }
  }
}