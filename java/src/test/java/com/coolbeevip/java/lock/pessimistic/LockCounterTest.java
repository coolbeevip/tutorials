package com.coolbeevip.java.lock.pessimistic;

import static org.hamcrest.MatcherAssert.assertThat;

import com.coolbeevip.java.lock.Counter;
import com.coolbeevip.java.lock.optimistic.AtomicCounter;
import com.coolbeevip.java.lock.optimistic.LongAccumulatorCounter;
import com.coolbeevip.java.lock.optimistic.LongAdderCounter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.hamcrest.Matchers;
import org.junit.Test;

public class LockCounterTest {

  private final static int NUM_THREADS = 1000;
  private final static long NUM_ITERATIONS = 10000;

  @Test
  public void testReentrantLockCounter() throws InterruptedException {
    test(new ReentrantLockCounter());
  }

  @Test
  public void testSynchronizedCounter() throws InterruptedException {
    test(new SynchronizedCounter());
  }

  @Test
  public void testAtomicCounter() throws InterruptedException {
    test(new AtomicCounter());
  }

  @Test
  public void testLongAdderCounter() throws InterruptedException {
    test(new LongAdderCounter());
  }

  @Test
  public void testLongAccumulatorCounterCounter() throws InterruptedException {
    test(new LongAccumulatorCounter());
  }

  @Test
  public void testReentrantWriteLockCounter() throws InterruptedException {
    test(new ReentrantReadWriteLockCounter());
  }

  @Test
  public void testStampedWriteLockCounter() throws InterruptedException {
    test(new StampedReadWriteLockCounter());
  }

  @Test
  public void testReentrantReadLockCounter() throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    ReentrantReadWriteLockCounter counter = new ReentrantReadWriteLockCounter();
    StopWatch watch = new StopWatch();
    watch.start();
    for (int i = 0; i < NUM_THREADS; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < NUM_ITERATIONS; i++) {
            counter.get();
          }
        }
      });
    }
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);
    watch.stop();
    assertThat(counter.get(), Matchers.is(0L));
  }

  @Test
  public void testStampedReadLockCounter() throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    StampedReadWriteLockCounter counter = new StampedReadWriteLockCounter();
    StopWatch watch = new StopWatch();
    watch.start();
    for (int i = 0; i < NUM_THREADS; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < NUM_ITERATIONS; i++) {
            counter.get();
          }
        }
      });
    }
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);
    watch.stop();
    assertThat(counter.get(), Matchers.is(0L));
  }

  @Test
  public void testStampedOptimisticReadLockCounter() throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    StampedReadWriteLockCounter counter = new StampedReadWriteLockCounter();
    StopWatch watch = new StopWatch();
    watch.start();
    for (int i = 0; i < NUM_THREADS; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < NUM_ITERATIONS; i++) {
            counter.optimisticGet();
          }
        }
      });
    }
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);
    watch.stop();
    assertThat(counter.get(), Matchers.is(0L));
  }

  private void test(final Counter counter) throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    for (int i = 0; i < NUM_THREADS; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < NUM_ITERATIONS; i++) {
            counter.increment();
          }
        }
      });
    }

    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.SECONDS);
    assertThat(counter.get(), Matchers.is(NUM_THREADS * NUM_ITERATIONS));
  }
}