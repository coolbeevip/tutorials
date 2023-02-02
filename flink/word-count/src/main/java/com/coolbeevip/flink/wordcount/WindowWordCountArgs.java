package com.coolbeevip.flink.wordcount;

import com.beust.jcommander.Parameter;

public class WindowWordCountArgs {

  @Parameter(names = "--env", description = "运行环境")
  public EnvironmentPolicy.EnvironmentEnum environment = EnvironmentPolicy.EnvironmentEnum.STANDARD;

  @Parameter(names = "--host", description = "监听地址")
  public String host = "localhost";

  @Parameter(names = "--port", description = "监听端口")
  public Integer port = 9999;
}