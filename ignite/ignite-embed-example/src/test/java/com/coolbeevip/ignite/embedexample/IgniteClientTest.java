package com.coolbeevip.ignite.embedexample;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteAtomicLong;
import org.apache.ignite.IgniteQueue;
import org.apache.ignite.IgniteSet;
import org.apache.ignite.cache.CacheMode;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class IgniteClientTest {

  static String keystoreFile = "/Users/zhanglei/Work/github/tutorials/ignite/ignite-embed-example/src/main/resources/keystore/keystore.jks";
  static String keystorePass = "123456";
  static String truststoreFile = "/Users/zhanglei/Work/github/tutorials/ignite/ignite-embed-example/src/main/resources/keystore/truststore.jks";
  static String truststorePass = "123456";
  static boolean clientMode = true;

  List<IgniteNode> servers = new ArrayList<>();

  static {
    System.setProperty("java.net.preferIPv4Stack", "true");
    System.setProperty("IGNITE_QUIET", "true");
  }

  @Test
  public void testAtomicLong() throws InterruptedException {
    try (IgniteNode node = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57502, 3,
        Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
        keystorePass,
        truststoreFile, truststorePass)) {
      node.createAtomicLong("counter").compareAndSet(node.createAtomicLong("counter").get(), 0);

      Thread threadAdd = new Thread(() -> {
        log.info("threadAdd begin");
        try (IgniteNode node1 = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57500,
            3,
            Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
            keystorePass,
            truststoreFile, truststorePass)) {
          IgniteAtomicLong counter = node1.createAtomicLong("counter");
          IntStream.range(0, 1000).forEach(n -> counter.addAndGet(1));
          log.info("threadAdd end");
        }
      }, "threadAdd");

      Thread threadDecrement = new Thread(() -> {
        log.info("threadDecrement begin");
        try (IgniteNode node2 = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57501,
            3,
            Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
            keystorePass,
            truststoreFile, truststorePass)) {
          IgniteAtomicLong counter = node2.createAtomicLong("counter");
          IntStream.range(0, 1000).forEach(n -> counter.decrementAndGet());
          log.info("threadDecrement end");
        }
      }, "threadDecrement");

      Thread chaosThread = chaosServer();
      threadAdd.start();
      threadDecrement.start();
      chaosThread.start();
      threadAdd.join();
      threadDecrement.join();
      chaosThread.join();

      assertThat(node.createAtomicLong("counter").get(), Matchers.is(0L));
    }
  }

  @Test
  public void testQueue() throws InterruptedException {
    try (IgniteNode node = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57502, 3,
        Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
        keystorePass,
        truststoreFile, truststorePass)) {
      IgniteQueue<String> queue = node.getOrCreateQueue("myQueue", null, 0, CacheMode.PARTITIONED,
          1, false);
      queue.clear();

      Thread threadAdd = new Thread(() -> {
        try (IgniteNode node1 = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57500,
            3,
            Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
            keystorePass,
            truststoreFile, truststorePass)) {
          IgniteQueue<String> queue1 = node1.getOrCreateQueue("myQueue", null, 0,
              CacheMode.PARTITIONED, 1, false);
          IntStream.range(0, 1000).forEach(n -> queue1.put("Q_" + n));
        }
      }, "threadPut");

      Thread threadDecrement = new Thread(() -> {
        try (IgniteNode node2 = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57501,
            3,
            Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
            keystorePass,
            truststoreFile, truststorePass)) {
          IgniteQueue<String> queue2 = node2.getOrCreateQueue("myQueue", null, 0,
              CacheMode.PARTITIONED, 1, false);
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
      IgniteSet<String> set = node.getOrCreateSet("mySet", null, CacheMode.PARTITIONED, 1, false);
      set.clear();

      Thread threadAdd = new Thread(() -> {
        try (IgniteNode node1 = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57500,
            3,
            Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
            keystorePass,
            truststoreFile, truststorePass)) {
          IgniteSet<String> set1 = node1.getOrCreateSet("mySet", null, CacheMode.PARTITIONED, 1,
              false);
          IntStream.range(0, 1000).forEach(n -> set1.add("Q_" + n));
        }
      }, "threadPut");

      Thread threadDecrement = new Thread(() -> {
        try (IgniteNode node2 = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57501,
            3,
            Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
            keystorePass,
            truststoreFile, truststorePass)) {
          IgniteSet<String> set2 = node2.getOrCreateSet("mySet", null, CacheMode.PARTITIONED, 1,
              false);
          IntStream.range(0, 1000).forEach(n -> set2.add("Q_" + n));
        }
      }, "threadTake");

      threadAdd.start();
      threadDecrement.start();
      threadAdd.join();
      threadDecrement.join();

      assertThat(set.size(), Matchers.is(1000));
    }
  }

  private Thread chaosServer() {
    return new Thread(() -> {
      Random r = new Random();
      IgniteNode oldNode = servers.get(r.nextInt(2));
      log.info("chaos stop server node {}", oldNode.getId());
      oldNode.close();
      servers.remove(oldNode);
      IgniteNode newNode = startServer();
      servers.add(newNode);
      log.info("chaos start server node {}", newNode.getId());
    }, "chaosThread");
  }

  private IgniteNode startServer() {
    return IgniteNodeFactory.createIgniteNode(false, "127.0.0.1", 47500, 3,
        Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
        keystorePass,
        truststoreFile, truststorePass);
  }

  @Before
  public void setup() {
    IntStream.rangeClosed(0, 2).forEach(n -> {
      servers.add(startServer());
    });
  }

  @After
  public void tearDown() {
    servers.stream().forEach(s -> s.close());
  }
}