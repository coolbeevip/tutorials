package com.coolbeevip.ignite.embedexample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteAtomicLong;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.IgniteQueue;
import org.apache.ignite.IgniteSet;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.stream.StreamReceiver;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class IgniteClientTest {

  static String keystoreFile = "/Users/zhanglei/Work/github/tutorials/ignite/ignite-embed-example/src/main/resources/keystore/keystore.jks";
  static String keystorePass = "123456";
  static String truststoreFile = "/Users/zhanglei/Work/github/tutorials/ignite/ignite-embed-example/src/main/resources/keystore/truststore.jks";
  static String truststorePass = "123456";
  static boolean clientMode = true;

  static ObjectMapper mapper = new ObjectMapper();

  static List<IgniteNode> servers = new ArrayList<>();

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
      node.createOrGetAtomicLong("counter").compareAndSet(node.createOrGetAtomicLong("counter").get(), 0);

      Thread threadAdd = new Thread(() -> {
        log.info("threadAdd begin");
        try (IgniteNode node1 = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57500,
            3,
            Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
            keystorePass,
            truststoreFile, truststorePass)) {
          IgniteAtomicLong counter = node1.createOrGetAtomicLong("counter");
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
          IgniteAtomicLong counter = node2.createOrGetAtomicLong("counter");
          IntStream.range(0, 1000).forEach(n -> counter.decrementAndGet());
          log.info("threadDecrement end");
        }
      }, "threadDecrement");

      threadAdd.start();
      threadDecrement.start();
      threadAdd.join();
      threadDecrement.join();
      IgniteAtomicLong counter = node.createOrGetAtomicLong("counter");
      assertThat(counter, Matchers.is(Matchers.notNullValue()));
      assertThat(counter.get(), Matchers.is(0L));
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

  @Test
  public void testStream() {
    Faker faker = new Faker();
    try (IgniteNode node = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57502, 3,
        Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
        keystorePass,
        truststoreFile, truststorePass)) {

      IgniteCache cache = node.createCache("myStream", CacheMode.PARTITIONED, 1, CacheWriteSynchronizationMode.FULL_SYNC);

      IgniteDataStreamer<Integer, String> sourceStreamer = node.dataStreamer(cache.getName());
      IgniteDataStreamer<Integer, String> sinkStreamer = node.dataStreamer(cache.getName());

      try {
        sinkStreamer.allowOverwrite(true);
        sinkStreamer.receiver((StreamReceiver<Integer, String>) (cache2, entries) -> entries.forEach(entry -> {
          System.out.println(String.format("<<<< %d %s", entry.getKey(), entry.getValue()));
          cache2.put(entry.getKey(), entry.getValue());
        }));

        IntStream.range(0, 1000).forEach(n -> {
          Employee employee = new Employee(faker.name().fullName(),
              faker.country().name(),
              faker.address().state(),
              faker.address().city(),
              faker.address().fullAddress());
          System.out.println(String.format(">>>> %d %s", n, employee.toJSON()));
          sourceStreamer.addData(n, employee.toJSON());
        });
        sourceStreamer.flush();
        try {
          TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      } finally {
        if (sinkStreamer != null) sourceStreamer.close();
        if (sourceStreamer != null) sourceStreamer.close();
      }
    }
  }

  private static IgniteNode startServer() {
    return IgniteNodeFactory.createIgniteNode(false, "127.0.0.1", 47500, 3,
        Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
        keystorePass,
        truststoreFile, truststorePass);
  }

  @BeforeClass
  public static void setup() {
    IntStream.rangeClosed(0, 2).forEach(n -> {
      servers.add(startServer());
    });
  }

  @AfterClass
  public static void tearDown() {
    servers.stream().forEach(s -> s.close());
  }

  @Data
  class Employee implements Serializable {
    String name;
    String country;
    String state;
    String city;
    String address;

    public Employee(String name, String country, String state, String city, String address) {
      this.name = name;
      this.country = country;
      this.state = state;
      this.city = city;
      this.address = address;
    }

    public String toJSON() {
      try {
        return mapper.writeValueAsString(this);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }
  }
}