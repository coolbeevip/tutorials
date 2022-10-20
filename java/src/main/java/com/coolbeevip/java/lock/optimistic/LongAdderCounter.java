package com.coolbeevip.java.lock.optimistic;

import com.coolbeevip.java.lock.Counter;

import java.util.concurrent.atomic.LongAdder;

/**
 * 使用分组的方式减少锁粒度，在多线程下效率更优
 */
public class LongAdderCounter implements Counter {

  private LongAdder counter = new LongAdder();

  public void increment() {
    mockTime();
    counter.increment();
  }

  public long get() {
    mockTime();
    return counter.sum();
  }
}