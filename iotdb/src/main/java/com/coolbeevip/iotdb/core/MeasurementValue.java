package com.coolbeevip.iotdb.core;

public class MeasurementValue {
  public final long timestamp;
  public final double value;

  public MeasurementValue(long timestamp, double value) {
    this.timestamp = timestamp;
    this.value = value;
  }
}