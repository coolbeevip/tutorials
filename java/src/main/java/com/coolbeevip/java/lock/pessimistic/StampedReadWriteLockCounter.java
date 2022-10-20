package com.coolbeevip.java.lock.pessimistic;

import com.coolbeevip.java.lock.Counter;

import java.util.concurrent.locks.StampedLock;

/**
 * 进一步优化了读操作，支持乐观读锁 tryOptimisticRead
 *
 */
public class StampedReadWriteLockCounter implements Counter {

  private StampedLock lock = new StampedLock();
  private volatile long counter;

  public void increment() {
    long stamp = lock.writeLock();
    try {
      mockTime();
      counter++;
    } finally {
      lock.unlockWrite(stamp);
    }
  }

  public long get() {
    long stamp = lock.readLock();
    try {
      mockTime();
      return counter;
    } finally {
      lock.unlockRead(stamp);
    }
  }

  public long optimisticGet() {
    long stamp = lock.tryOptimisticRead(); // 获得戳
    long value = counter; // 读取数据
    if (!lock.validate(stamp)) { // 验证戳是否污染
      stamp = lock.readLock(); // 验证戳已被污染，获取读锁
      try {
        mockTime();
        return counter; // 返回结果
      } finally {
        lock.unlock(stamp); // 释放读锁
      }
    }
    return value; // 验证戳未被写污染，直接返回结果
  }
}