package com.coolbeevip.ignite.jmh;

import com.coolbeevip.ignite.embedexample.IgniteNode;
import com.coolbeevip.ignite.embedexample.IgniteNodeFactory;
import org.apache.ignite.IgniteQueue;
import org.apache.ignite.cache.CacheMode;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Fork(value = 1, warmups = 2)
@Threads(4)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@OutputTimeUnit(TimeUnit.SECONDS)
public class IgniteQueueJMH {

  static String keystoreFile = "/Users/zhanglei/coolbeevip/tutorials/ignite/src/test/resources/keystore/keystore.jks";
  static String keystorePass = "123456";
  static String truststoreFile = "/Users/zhanglei/coolbeevip/tutorials/ignite/src/test/resources/keystore/truststore.jks";
  static String truststorePass = "123456";
  static boolean clientMode = true;

  @Benchmark
  public void queuePartitionedBackups_0(BenchmarkState state) {
    state.queue_B0.put(UUID.randomUUID().toString());
  }

  @Benchmark
  public void queuePartitionedBackups_1(BenchmarkState state) {
    state.queue_B1.put(UUID.randomUUID().toString());
  }

  @Benchmark
  public void queuePartitionedBackups_2(BenchmarkState state) {
    state.queue_B2.put(UUID.randomUUID().toString());
  }

  @Benchmark
  public void queueReplicatedBackups_0(BenchmarkState state) {
    state.queue_R_B0.put(UUID.randomUUID().toString());
  }

  @Benchmark
  public void queueReplicatedBackups_1(BenchmarkState state) {
    state.queue_R_B1.put(UUID.randomUUID().toString());
  }

  @Benchmark
  public void queueReplicatedBackups_2(BenchmarkState state) {
    state.queue_R_B2.put(UUID.randomUUID().toString());
  }

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    IgniteNode node;
    IgniteQueue<String> queue_B0;
    IgniteQueue<String> queue_B1;
    IgniteQueue<String> queue_B2;
    IgniteQueue<String> queue_R_B0;
    IgniteQueue<String> queue_R_B1;
    IgniteQueue<String> queue_R_B2;

    @Setup(Level.Trial)
    public void initialize() {
      node = IgniteNodeFactory.createIgniteNode(clientMode, "127.0.0.1", 57502, 3,
          Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502"), keystoreFile,
          keystorePass,
          truststoreFile, truststorePass);
      queue_B0 = node.getOrCreateQueue("queue_B0","group_queue_B0",0, CacheMode.PARTITIONED,0,true);
      queue_B1 = node.getOrCreateQueue("queue_B1","group_queue_B1",0, CacheMode.PARTITIONED,1,true);
      queue_B2 = node.getOrCreateQueue("queue_B2","group_queue_B2",0, CacheMode.PARTITIONED,2,true);
      queue_R_B0 = node.getOrCreateQueue("queue_R_B0","group_queue_R_B0",0, CacheMode.REPLICATED,0,true);
      queue_R_B1 = node.getOrCreateQueue("queue_R_B1","group_queue_R_B1",0, CacheMode.REPLICATED,1,true);
      queue_R_B2 = node.getOrCreateQueue("queue_R_B2","group_queue_R_B2",0, CacheMode.REPLICATED,2,true);
    }

    @TearDown
    public void tearDown() {
      queue_B0.close();
      queue_B1.close();
      queue_B2.close();
      queue_R_B0.close();
      queue_R_B1.close();
      queue_R_B2.close();
      node.close();
    }
  }
}