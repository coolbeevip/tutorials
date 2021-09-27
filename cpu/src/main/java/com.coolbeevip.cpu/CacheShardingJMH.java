package com.coolbeevip.cpu;

import java.util.concurrent.TimeUnit;
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

/**
 * 通过二维数据的遍历方式演示连续内存访问时 Cache Line 的效果
 */
@BenchmarkMode(Mode.All)
@Fork(value = 1, warmups = 2)
@Threads(4)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CacheShardingJMH {

  static int ARRAY_X = 1024 * 1024;
  static int ARRAY_Y = 8;

  @Benchmark
  public void loopWithCacheSharing(BenchmarkState state) {
    for (int x = 0; x < ARRAY_X; x += 1) {
      for (int y = 0; y < ARRAY_Y; y++) {
        long value = state.array[x][y];
      }
    }
  }

  @Benchmark
  public void loopWithNoCacheSharing(BenchmarkState state) {
    for (int y = 0; y < ARRAY_Y; y += 1) {
      for (int x = 0; x < ARRAY_X; x++) {
        long value = state.array[x][y];
      }
    }
  }

  @Benchmark
  public void loopWithFalseCacheSharing(BenchmarkState state) {
    for (int x = 0; x < ARRAY_X; x += 1) {
      for (int y = 0; y < ARRAY_Y; y++) {
        state.array[x][y]=-1;
      }
    }
  }

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    long[][] array;

    @Setup(Level.Trial)
    public void initialize() {
      array = new long[ARRAY_X][];
      for (int i = 0; i < ARRAY_X; i++) {
        array[i] = new long[ARRAY_Y];
        for (int j = 0; j < ARRAY_Y; j++) {
          array[i][j] = 0L;
        }
      }
    }

    @TearDown
    public void tearDown() {

    }
  }
}