package com.coolbeevip.java.lock.optimistic;

import com.coolbeevip.java.lock.Counter;
import java.util.concurrent.atomic.LongAccumulator;

/**
 * 乐观锁
 * 支持2个运算符，本例子通过 sum 实现累加
 */
public class LongAccumulatorCounter implements Counter {

  private LongAccumulator counter = new LongAccumulator(Long::sum,0);

  public void increment() {
    counter.accumulate(1);
  }

  public long get() {
    return counter.get();
  }
}