package com.coolbeevip.ignite;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;
import javax.cache.Cache.Entry;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;

@Slf4j
public class IgniteServer {

  static String keystoreFile = "keystore/keystore.jks";
  static String keystorePass = "123456";
  static String truststoreFile = "keystore/truststore.jks";
  static String truststorePass = "123456";
  static boolean clientMode = false;


  public static void main(String[] args) throws Exception {
    //IgniteNode node = IgniteNodeFactory.createIgniteNode("228.10.10.158");
    IgniteNode node = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 47500, 3,
        Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
        keystorePass,
        truststoreFile, truststorePass);

    while (true) {
      log.info(node.info());
      Thread.sleep(60000);
    }
  }
}