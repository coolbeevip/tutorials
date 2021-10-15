package com.coolbeevip.ignite;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.stream.IntStream;
import org.apache.ignite.IgniteAtomicLong;
import org.hamcrest.Matchers;
import org.junit.Test;

public class IgniteClientTest {

  static String keystoreFile = "/Users/zhanglei/coolbeevip/tutorials/ignite/src/test/resources/keystore/keystore.jks";
  static String keystorePass = "123456";
  static String truststoreFile = "/Users/zhanglei/coolbeevip/tutorials/ignite/src/test/resources/keystore/truststore.jks";
  static String truststorePass = "123456";
  static boolean clientMode = true;

  @Test
  public void testAtomicLong() throws InterruptedException {
    try (IgniteNode node = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57502, 3,
        Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
        keystorePass,
        truststoreFile, truststorePass)) {
      node.createAtomicLong("counter").compareAndSet(node.createAtomicLong("counter").get(),0);

      Thread threadAdd = new Thread(() -> {
        try (IgniteNode node1 = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57500, 3,
            Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
            keystorePass,
            truststoreFile, truststorePass)) {
          IgniteAtomicLong counter = node1.createAtomicLong("counter");
          IntStream.range(0, 1000).forEach(n -> counter.addAndGet(1));
        }
      }, "threadAdd");

      Thread threadDecrement = new Thread(() -> {
        try (IgniteNode node2 = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57501, 3,
            Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
            keystorePass,
            truststoreFile, truststorePass)) {
          IgniteAtomicLong counter = node2.createAtomicLong("counter");
          IntStream.range(0, 1000).forEach(n -> counter.decrementAndGet());
        }
      }, "threadDecrement");

      threadAdd.start();
      threadDecrement.start();
      threadAdd.join();
      threadDecrement.join();

      assertThat(node.createAtomicLong("counter").get(), Matchers.is(0L));
    }
  }
}