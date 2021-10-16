package com.coolbeevip.ignite;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.stream.IntStream;
import org.apache.ignite.IgniteAtomicLong;
import org.apache.ignite.IgniteQueue;
import org.apache.ignite.IgniteSet;
import org.apache.ignite.cache.CacheMode;
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

  @Test
  public void testQueue() throws InterruptedException {
    try (IgniteNode node = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57502, 3,
        Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
        keystorePass,
        truststoreFile, truststorePass)) {
      IgniteQueue<String> queue = node.getOrCreateQueue("myQueue",null,0, CacheMode.PARTITIONED,1,false);
      queue.clear();

      Thread threadAdd = new Thread(() -> {
        try (IgniteNode node1 = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57500, 3,
            Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
            keystorePass,
            truststoreFile, truststorePass)) {
          IgniteQueue<String> queue1 = node1.getOrCreateQueue("myQueue",null,0, CacheMode.PARTITIONED,1,false);
          IntStream.range(0, 1000).forEach(n -> queue1.put("Q_"+n));
        }
      }, "threadPut");

      Thread threadDecrement = new Thread(() -> {
        try (IgniteNode node2 = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57501, 3,
            Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
            keystorePass,
            truststoreFile, truststorePass)) {
          IgniteQueue<String> queue2 = node2.getOrCreateQueue("myQueue",null,0, CacheMode.PARTITIONED,1,false);
          IntStream.range(0, 1000).forEach(n -> queue2.take());
        }
      }, "threadTake");

      threadAdd.start();
      threadDecrement.start();
      threadAdd.join();
      threadDecrement.join();

      assertThat(queue.size(), Matchers.is(0));
    }
  }

  @Test
  public void testSet() throws InterruptedException {
    try (IgniteNode node = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57502, 3,
        Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
        keystorePass,
        truststoreFile, truststorePass)) {
      IgniteSet<String> set = node.getOrCreateSet("mySet", null,CacheMode.PARTITIONED,1,false);
      set.clear();

      Thread threadAdd = new Thread(() -> {
        try (IgniteNode node1 = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57500, 3,
            Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
            keystorePass,
            truststoreFile, truststorePass)) {
          IgniteSet<String> set1 = node1.getOrCreateSet("mySet",null, CacheMode.PARTITIONED,1,false);
          IntStream.range(0, 1000).forEach(n -> set1.add("Q_"+n));
        }
      }, "threadPut");

      Thread threadDecrement = new Thread(() -> {
        try (IgniteNode node2 = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57501, 3,
            Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
            keystorePass,
            truststoreFile, truststorePass)) {
          IgniteSet<String> set2 = node2.getOrCreateSet("mySet", null,CacheMode.PARTITIONED,1,false);
          IntStream.range(0, 1000).forEach(n -> set2.add("Q_"+n));
        }
      }, "threadTake");

      threadAdd.start();
      threadDecrement.start();
      threadAdd.join();
      threadDecrement.join();

      assertThat(set.size(), Matchers.is(1000));
    }
  }
}