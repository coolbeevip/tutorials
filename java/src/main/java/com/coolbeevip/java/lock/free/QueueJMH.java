package com.coolbeevip.java.lock.free;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

/**
 *
 */
@State(Scope.Group)
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1, warmups = 2)
@Threads(4)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@OutputTimeUnit(TimeUnit.SECONDS)
public class QueueJMH {

  private final static int NUM_THREADS = 1000;
  private final static long NUM_ITERATIONS = 1;

  //final NonBlockingQueue<String> nonBlockingQueue = new NonBlockingQueue();
  //final ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(2);
  //final LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
  //final ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();


  @Benchmark
  @Group("nonBlockingQueue")
  public void nonBlockingQueue() throws InterruptedException {
    final NonBlockingQueue<String> nonBlockingQueue = new NonBlockingQueue();
    final ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    for (int i = 0; i < NUM_THREADS; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          nonBlockingQueue.add(UUID.randomUUID().toString());
        }
      });
    }
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);
  }

  @Benchmark
  @Group("linkedBlockingQueue")
  public void linkedBlockingQueue() throws InterruptedException {
    LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    for (int i = 0; i < NUM_THREADS; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          linkedBlockingQueue.offer(UUID.randomUUID().toString());
        }
      });
    }
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);
  }

  @Benchmark
  @Group("arrayBlockingQueue")
  public void arrayBlockingQueue() throws InterruptedException {
    ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(1000);
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    for (int i = 0; i < NUM_THREADS; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          arrayBlockingQueue.add(UUID.randomUUID().toString());
        }
      });
    }
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);
  }

  @Benchmark
  @Group("concurrentLinkedQueue")
  public void concurrentLinkedQueue() throws InterruptedException {
    final ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    for (int i = 0; i < NUM_THREADS; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          concurrentLinkedQueue.add(UUID.randomUUID().toString());
        }
      });
    }
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);
  }
}