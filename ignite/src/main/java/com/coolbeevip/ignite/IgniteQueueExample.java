package com.coolbeevip.ignite;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IgniteQueueExample {

  static String keystoreFile = "keystore/keystore.jks";
  static String keystorePass = "123456";
  static String truststoreFile = "keystore/truststore.jks";
  static String truststorePass = "123456";


  public static void main(String[] args) throws Exception {
    //IgniteNode node = IgniteNodeFactory.createIgniteNode("228.10.10.158");
    IgniteNode node = IgniteNodeFactory.createIgniteNode("127.0.0.1", 47500, 3,
        Arrays.asList("127.0.0.1:47500","127.0.0.1:47501","127.0.0.1:47502"), keystoreFile, keystorePass,
        truststoreFile, truststorePass);
    while (true) {
      log.info(node.info());
      Thread.sleep(5000);
    }
  }

}