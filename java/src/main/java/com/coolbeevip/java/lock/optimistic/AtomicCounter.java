package com.coolbeevip.java.lock.optimistic;

import com.coolbeevip.java.lock.Counter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 乐观锁
 * 适合读操作多的场景，不加锁的特点能够使其读操作的性能大幅提升
 */
public class AtomicCounter implements Counter {

  private AtomicLong counter = new AtomicLong();

  public void increment() {
    mockTime();
    counter.incrementAndGet();
  }

  public long get() {
    mockTime();
    return counter.get();
  }
}