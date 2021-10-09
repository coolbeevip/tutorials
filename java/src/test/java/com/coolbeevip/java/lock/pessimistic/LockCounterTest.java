package com.coolbeevip.java.lock.pessimistic;

import static org.hamcrest.MatcherAssert.assertThat;

import com.coolbeevip.java.lock.optimistic.AtomicCounter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Test;

public class LockCounterTest {

  private final static int NUM_THREADS = 1000;
  private final static long NUM_ITERATIONS = 1000;

  @Test
  public void testReentrantLockCounter() throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    final ReentrantLockCounter counter = new ReentrantLockCounter();

    for (int i = 0; i < NUM_THREADS; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < NUM_ITERATIONS; i++) {
            counter.incrementAndGet();
            counter.get();
          }
        }
      });
    }

    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);
    assertThat(counter.get(), Matchers.is(NUM_THREADS * NUM_ITERATIONS));
  }

  @Test
  public void testSynchronizedCounter() throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    final SynchronizedCounter counter = new SynchronizedCounter();

    for (int i = 0; i < NUM_THREADS; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < NUM_ITERATIONS; i++) {
            counter.incrementAndGet();
            counter.get();
          }
        }
      });
    }

    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);
    assertThat(counter.get(), Matchers.is(NUM_THREADS * NUM_ITERATIONS));
  }

  @Test
  public void testAtomicCounter() throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    final AtomicCounter counter = new AtomicCounter();

    for (int i = 0; i < NUM_THREADS; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < NUM_ITERATIONS; i++) {
            counter.incrementAndGet();
            counter.get();
          }
        }
      });
    }

    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);
    assertThat(counter.get(), Matchers.is(NUM_THREADS * NUM_ITERATIONS));
  }
}