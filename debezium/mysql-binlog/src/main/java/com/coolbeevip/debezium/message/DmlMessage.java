package com.coolbeevip.debezium.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class DmlMessage {

  private ObjectMapper mapper = new ObjectMapper();

  private String dbName;
  private String table;
  private String operation;
  private Long timestamp;
  private Map<String, Object> key;
  private Map<String, Pair<Object, Object>> payloadChanged;
  private Map<String, Object> payload;

  public String getDbName() {
    return dbName;
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Map<String, Object> getPayload() {
    return payload;
  }

  public void setPayload(Map<String, Object> payload) {
    this.payload = payload;
  }

  public Map<String, Pair<Object, Object>> getPayloadChanged() {
    return payloadChanged;
  }

  public void setPayloadChanged(Map<String, Pair<Object, Object>> payloadChanged) {
    this.payloadChanged = payloadChanged;
  }

  public Map<String, Object> getKey() {
    return key;
  }

  public void setKey(Map<String, Object> key) {
    this.key = key;
  }

  public String toJSON() {
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}