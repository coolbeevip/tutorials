package com.coolbeevip.ratis.sequence;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@Slf4j
public class SequenceBenchmark extends TestKit {

  @Test
  @SneakyThrows
  public void launchBenchmark() {
    Options opt = new OptionsBuilder()
        // Specify which benchmarks to run.
        // You can be more specific if you'd like to run only one benchmark per test.
        .include(this.getClass().getName() + ".*")
        // Set the following options as needed
        //.mode(Mode.AverageTime)
        //.timeUnit(TimeUnit.MICROSECONDS)
        //.warmupTime(TimeValue.seconds(1))
        //.warmupIterations(2)
        .measurementTime(TimeValue.seconds(1))
        .measurementIterations(2)
        .threads(2)
        //.forks(1)
        .shouldFailOnError(true)
        .shouldDoGC(true)
        //.jvmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining")
        //.addProfiler(WinPerfAsmProfiler.class)
        .build();

    new Runner(opt).run();
  }


  @Benchmark
  @Fork(value = 1, warmups = 5)
  @Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  public void genericId(BenchmarkState state, Blackhole bh) throws IOException {
    //System.out.println(">>>>>>>>>>" + state.sequence.genericId());
    state.sequence.genericId();
    state.counter.incrementAndGet();
  }

//  @TearDown
//  public static void tearDown(BenchmarkState state){
//    log.info("sequence counter is {}",state.counter.get());
//  }

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    Sequence sequence;
    AtomicLong counter = new AtomicLong();

    @Setup(Level.Trial)
    public void initialize() {
      sequence = Sequence.builder().peerAddress(Arrays.asList(peerAddress.split(",")))
          .build();
      CompletableFuture<Void> future = sequence.start();
      future.join();
    }
  }
}