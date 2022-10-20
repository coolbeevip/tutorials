package com.coolbeevip.cpu;

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

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.All)
@Fork(value = 1, warmups = 1)
@Threads(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class FalseCacheShardingJMH {

  static long ITERATIONS = 500_000_000L;
  static int NUM_THREADS = 4;

  @Benchmark
  public void noPaddingValues(BenchmarkState state) throws InterruptedException {
    Thread[] threads = new Thread[NUM_THREADS];
    for (int i = 0; i < NUM_THREADS; i++) {
      int finalI = i;
      Runnable r = new Runnable() {
        @Override
        public void run() {
          long i = ITERATIONS + 1;
          while (0 != --i) {
            state.noPaddingValues[finalI].value = i;
          }
        }
      };
      threads[i] = new Thread(r);
    }

    for (Thread t : threads) {
      t.start();
    }
    for (Thread t : threads) {
      t.join();
    }
  }

  @Benchmark
  public void paddingValues(BenchmarkState state) throws InterruptedException {
    Thread[] threads = new Thread[NUM_THREADS];
    for (int i = 0; i < NUM_THREADS; i++) {
      int finalI = i;
      Runnable r = new Runnable() {
        @Override
        public void run() {
          long i = ITERATIONS + 1;
          while (0 != --i) {
            state.paddingValues[finalI].value = i;
          }
        }
      };
      threads[i] = new Thread(r);
    }

    for (Thread t : threads) {
      t.start();
    }
    for (Thread t : threads) {
      t.join();
    }
  }

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    NoPaddingValue[] noPaddingValues = new NoPaddingValue[NUM_THREADS];
    PaddingValue[] paddingValues = new PaddingValue[NUM_THREADS];

    @Setup(Level.Trial)
    public void initialize() {
      for (int i = 0; i < NUM_THREADS; i++) {
        noPaddingValues[i] = new NoPaddingValue();
      }

      for (int i = 0; i < NUM_THREADS; i++) {
        paddingValues[i] = new PaddingValue();
      }
    }

    @TearDown
    public void tearDown() {

    }
  }

  public final static class NoPaddingValue {
    public volatile long value = 0L;
  }

  public final static class PaddingValue {
    public long p1, p2, p3, p4, p5, p6, p7, p8;
    public volatile long value = 0L;
    public long pa, pb, pc, pd, pe, pf, pg, ph;
  }
}