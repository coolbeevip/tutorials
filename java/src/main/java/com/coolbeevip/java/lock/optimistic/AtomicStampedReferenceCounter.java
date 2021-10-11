package com.coolbeevip.java.lock.optimistic;

import com.coolbeevip.java.lock.Counter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 乐观锁
 * 解决 Atomic 原子操作中存在的 ABA 问题
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