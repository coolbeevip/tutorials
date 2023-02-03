package com.coolbeevip.flink.wordcount;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class EnvironmentPolicy {
  private StreamExecutionEnvironment see;

  public EnvironmentPolicy(EnvironmentEnum environmentEnum) {
    if (environmentEnum == EnvironmentEnum.LOCAL) {
      see = StreamExecutionEnvironment.createLocalEnvironment();
    } else if (environmentEnum == EnvironmentEnum.STANDARD) {
      see = StreamExecutionEnvironment.getExecutionEnvironment();
    } else {
      throw new UnsupportedOperationException("不支持环境 " + environmentEnum);
    }
  }

  public StreamExecutionEnvironment getSee() {
    return see;
  }

  public enum EnvironmentEnum {
    STANDARD, LOCAL, COLLECTION,
  }
}
