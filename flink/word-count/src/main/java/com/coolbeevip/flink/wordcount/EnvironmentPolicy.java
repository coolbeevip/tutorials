package com.coolbeevip.flink.wordcount;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class EnvironmentPolicy {
  private StreamExecutionEnvironment env;

  public EnvironmentPolicy(EnvironmentEnum environmentEnum) {
    if (environmentEnum == EnvironmentEnum.LOCAL) {
      env = StreamExecutionEnvironment.createLocalEnvironment();
    } else if (environmentEnum == EnvironmentEnum.STANDARD) {
      env = StreamExecutionEnvironment.getExecutionEnvironment();
    } else {
      throw new UnsupportedOperationException("不支持环境 " + environmentEnum);
    }
  }

  public StreamExecutionEnvironment getEnv() {
    return env;
  }

  public enum EnvironmentEnum {
    STANDARD, LOCAL, COLLECTION,
  }
}
