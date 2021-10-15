package com.coolbeevip.java.lock.free;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

/**
 *
 */
@BenchmarkMode({Mode.Throughput})
@Fork(value = 1, warmups = 2)
@Threads(4)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@OutputTimeUnit(TimeUnit.SECONDS)
public class QueueQPSJMH {

  private final static int NUM_THREADS = 1000_000_000;

  @Benchmark
  public void arrayBlockingQueue_1P1C() throws InterruptedException {
    final ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(1000);
    Thread thread_push = new Thread(() -> {
      IntStream.of(NUM_THREADS).forEach(n->{
        try {
          arrayBlockingQueue.put(n);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }, "thread_push");

    Thread thread_pop = new Thread(() -> {
      IntStream.of(NUM_THREADS).forEach(n->{
        try {
          arrayBlockingQueue.take();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }, "thread_pop");

    thread_push.start();
    thread_pop.start();
    thread_push.join();
    thread_pop.join();
  }

  @Benchmark
  public void arrayBlockingQueue_1P4C() throws InterruptedException {
    final ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(1000);
    Thread thread_push = new Thread(() -> {
      IntStream.of(NUM_THREADS).forEach(n->{
        try {
          arrayBlockingQueue.put(n);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }, "thread_push");

    Thread thread_pop1 = new Thread(() -> {
      IntStream.of(NUM_THREADS/4).forEach(n->{
        try {
          arrayBlockingQueue.take();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    });

    Thread thread_pop2 = new Thread(() -> {
      IntStream.of(NUM_THREADS/4).forEach(n->{
        try {
          arrayBlockingQueue.take();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    });

    Thread thread_pop3 = new Thread(() -> {
      IntStream.of(NUM_THREADS/4).forEach(n->{
        try {
          arrayBlockingQueue.take();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    });

    Thread thread_pop4 = new Thread(() -> {
      IntStream.of(NUM_THREADS/4).forEach(n->{
        try {
          arrayBlockingQueue.take();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    });


    thread_push.start();
    thread_pop1.start();
    thread_pop2.start();
    thread_pop3.start();
    thread_pop4.start();
    thread_pop1.join();
    thread_pop2.join();
    thread_pop3.join();
    thread_pop4.join();
  }

  @Benchmark
  public void linkedBlockingQueue_1P1C() throws InterruptedException {
    final LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
    Thread thread_push = new Thread(() -> {
      IntStream.of(NUM_THREADS).forEach(n->{
        try {
          linkedBlockingQueue.put(n);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }, "thread_push");

    Thread thread_pop = new Thread(() -> {
      IntStream.of(NUM_THREADS).forEach(n->{
        try {
          linkedBlockingQueue.take();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }, "thread_pop");

    thread_push.start();
    thread_pop.start();
    thread_push.join();
    thread_pop.join();
  }
}