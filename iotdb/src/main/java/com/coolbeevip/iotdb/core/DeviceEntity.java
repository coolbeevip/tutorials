package com.coolbeevip.iotdb.core;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.rpc.TSStatusCode;
import org.apache.iotdb.service.rpc.thrift.TSCreateTimeseriesReq;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.tsfile.file.metadata.enums.CompressionType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.apache.iotdb.tsfile.write.record.Tablet;
import org.apache.iotdb.tsfile.write.schema.MeasurementSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DeviceEntity {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  protected Session session;
  protected String storageGroup;
  protected Set<String> paths = new LinkedHashSet<>();
  protected Set<TSCreateTimeseriesReq> measurements = new LinkedHashSet<>();

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  public String getStorageGroup() {
    return storageGroup;
  }

  public void setStorageGroup(String storageGroup) {
    this.storageGroup = storageGroup;
  }

  public Set<String> getPaths() {
    return paths;
  }

  public void setPaths(Set<String> paths) {
    this.paths = paths;
  }

  public void setMeasurements(Set<TSCreateTimeseriesReq> measurements) {
    this.measurements = measurements;
  }

  public List<MeasurementSchema> getMeasurementSchemas() {
    return this.measurements.stream().map(
        o -> new MeasurementSchema(o.getPath().substring(o.getPath().lastIndexOf(".") + 1),
            TSDataType.deserialize((byte) o.dataType))).collect(Collectors.toList());
  }

  public void initStorageGroup() throws IoTDBConnectionException {
    try {
      this.session.setStorageGroup(storageGroup);
    } catch (StatementExecutionException e) {
      if (e.getStatusCode() != TSStatusCode.PATH_ALREADY_EXIST_ERROR.getStatusCode()) {
        log.info("StorageGroup {} exist", storageGroup);
      }
    }
  }

  public void initTimeSeries() {
    this.measurements.stream().forEach(o -> {
      try {
        if (!session.checkTimeseriesExists(o.getPath())) {
          session.createTimeseries(o.getPath(), TSDataType.DOUBLE, TSEncoding.GORILLA,
              CompressionType.SNAPPY, o.getProps(), o.getTags(), o.getAttributes(),
              o.getMeasurementAlias());
          log.info("TimeSeries {} created", o.getPath());
        } else {
          log.info("TimeSeries {} exist", o.getPath());
        }
      } catch (Exception ex) {
        log.error("create TimeSeries {} failed", o.getPath(), ex);
      }
    });
  }

  public String getDevicePath() {
    return this.storageGroup + "." + this.paths.stream().collect(Collectors.joining("."));
  }

  public void batchInsert(String measurementId, List<MeasurementValue> measurementValues,
      int batchSize) throws IoTDBConnectionException, StatementExecutionException {
    long begin = System.currentTimeMillis();
    List<MeasurementSchema> schemaList = this.getMeasurementSchemas();
    Tablet tablet = new Tablet(this.getDevicePath(), schemaList, batchSize);
    int row = 0;
    for (MeasurementValue mv : measurementValues) {
      int index = tablet.rowSize++;
      tablet.addTimestamp(index, mv.timestamp);
      tablet.addValue(measurementId, index, mv.value);
      if (tablet.rowSize == tablet.getMaxRowNumber()) {
        session.insertTablet(tablet);
        tablet.reset();
        log.info("batch insert {}", (row + 1));
      }
      row++;
    }
    if (tablet.rowSize != 0) {
      session.insertTablet(tablet);
      tablet.reset();
    }
    long end = System.currentTimeMillis();
    log.info("batch insert {} finished, time total {}ms", measurementValues.size(), end - begin);
  }
}