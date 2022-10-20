package com.coolbeevip.ignite.embedexample;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteQueue;
import org.apache.ignite.cache.CacheMode;

import java.util.Arrays;
import java.util.stream.IntStream;

@Slf4j
public class IgniteClient {

  static String keystoreFile = "keystore/keystore.jks";
  static String keystorePass = "123456";
  static String truststoreFile = "keystore/truststore.jks";
  static String truststorePass = "123456";

  static IgniteNode client;
  public static void main(String[] args) {
    client = IgniteNodeFactory.createIgniteNode(true, "127.0.0.1", 47500, 3,
        Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
        keystorePass,
        truststoreFile, truststorePass);
    //put();
    get();
    client.close();
  }

  public static void put(){
    IgniteQueue queue = client.getOrCreateQueue("queue_test1", null, 0, CacheMode.REPLICATED,
        3, false);
    IntStream.range(0, 10).forEach(n -> queue.put(n));
    log.info("queue size {}",queue.size());
  }

  public static void get(){
    IgniteQueue queue = client.getOrCreateQueue("queue_test1", null, 0, CacheMode.REPLICATED,
        3, false);
    log.info("queue size {}",queue.size());
  }
}