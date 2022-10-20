package com.coolbeevip.debezium.binlog;

import io.debezium.config.Configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class DebeziumMain {

  public static void main(String[] args) throws InterruptedException, IOException {

    CountDownLatch mainLatch = new CountDownLatch(1);

    DebeziumFactory factory = new DebeziumFactory();
    Configuration configuration = factory
        .configuration("192.168.51.206", 3810, "root", "a_89tlXh7o", "nc_notifier");
    DebeziumBinlogListener listener = new DebeziumBinlogListener(configuration, Arrays.asList("customer"));
    listener.start();

    mainLatch.await();
  }
}