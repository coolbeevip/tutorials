package com.coolbeevip.java.lock.pessimistic;

import com.coolbeevip.java.lock.Counter;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;

/**
 * 悲观锁
 *
 */
public class StampedReadWriteLockCounter implements Counter {

  private StampedLock lock = new StampedLock();
  private volatile long counter;

  public void increment() {
    long stamp = lock.writeLock();
    try {
      counter++;
    } finally {
      lock.unlockWrite(stamp);
    }
  }

  public long get() {
    long stamp = lock.readLock();
    try {
      return counter;
    } finally {
      lock.unlockRead(stamp);
    }
  }

  public long optimisticGet() {
    long stamp = lock.tryOptimisticRead();
    if (!lock.validate(stamp)) {
      stamp = lock.readLock();
      try {
        return counter;
      } finally {
        lock.unlock(stamp);
      }
    }
    return counter;
  }
}