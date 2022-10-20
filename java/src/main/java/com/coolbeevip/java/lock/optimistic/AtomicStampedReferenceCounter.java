package com.coolbeevip.java.lock.optimistic;

import com.coolbeevip.java.lock.Counter;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 乐观锁
 * 解决 Atomic 原子操作中存在的 ABA 问题
 * 这里仅仅演示使用方法，本身并无实际意义，通常 ABA 问题都反映在一个堆栈或者队列中
 * https://www.baeldung.com/cs/aba-concurrency
 */
public class AtomicStampedReferenceCounter implements Counter {

  private AtomicStampedReference<Long> counter = new AtomicStampedReference(0l, 1);

  public void increment() {
    mockTime();
    while (!counter
        .compareAndSet(counter.getReference(), counter.getReference() + 1, counter.getStamp(),
            counter.getStamp() + 1)) {
      //
    }
    ;
  }

  public long get() {
    mockTime();
    return counter.getReference();
  }
}