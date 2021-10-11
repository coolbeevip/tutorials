package com.coolbeevip.java.lock.pessimistic;

import com.coolbeevip.java.lock.Counter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 悲观锁
 * 读写锁分离适合读多写少的场景
 * - 读-读,不互斥
 * - 读-写,互斥
 * - 写-写,互斥
 */
public class ReentrantReadWriteLockCounter implements Counter {

  private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  private volatile long counter;

  public void increment() {
    Lock writeLock = lock.writeLock(); // 写锁语意和 ReentrantLock 类似
    writeLock.lock();
    try {
      counter++;
      mockTime();
    } finally {
      writeLock.unlock();
    }
  }

  public long get() {
    Lock readLock = lock.readLock();
    readLock.lock();
    try {
      mockTime();
      return counter;
    } finally {
      readLock.unlock();
    }
  }
}