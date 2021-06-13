package com.coolbeevip.ratis.sequence.benchmarks;

import com.coolbeevip.ratis.sequence.Sequence;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@Slf4j
public class SequenceBenchmark extends ClusterKit {

  @SneakyThrows
  public static void main(String[] args) {
    startClusters();
    Options opt = new OptionsBuilder()
        // Specify which benchmarks to run.
        // You can be more specific if you'd like to run only one benchmark per test.
        .include(SequenceBenchmark.class.getName() + ".*")
        .resultFormat(ResultFormatType.JSON)
        .result("jmh-" + System.currentTimeMillis() + ".json")
        .shouldFailOnError(true)
        .shouldDoGC(true)
        //.jvmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining")
        //.addProfiler(WinPerfAsmProfiler.class)
        .build();

    new Runner(opt).run();
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @Fork(value = 1, warmups = 5)
  @Threads(4)
  @Warmup(iterations = 5, time = 1)
  @Measurement(iterations = 10, time = 5)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  public void genericId(BenchmarkState state, Blackhole bh) throws IOException {
    state.sequence.genericId();
    state.counter.incrementAndGet();
  }

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    Sequence sequence;
    AtomicLong counter = new AtomicLong();

    @Setup(Level.Trial)
    public void initialize() {
      sequence = Sequence.builder().peerAddress(peerAddress)
          .build();
      CompletableFuture<Void> future = sequence.start();
      future.join();
    }

    @TearDown
    public void tearDown() {
      log.info("sequence counter is {}", counter.get());
    }
  }
}