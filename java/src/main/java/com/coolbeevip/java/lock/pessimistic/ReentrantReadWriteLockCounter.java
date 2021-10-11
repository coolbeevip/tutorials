package com.coolbeevip.java.lock.pessimistic;

import com.coolbeevip.java.lock.Counter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 悲观锁
 * 读写锁分离适合读多写少的场景
 * - 读-读不互斥
 * - 读-写互斥
 * - 写-写互斥
 */
public class ReentrantReadWriteLockCounter implements Counter {

  private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  private volatile long counter;

  public void increment() {
    System.out.println("begin-"+Thread.currentThread().getId());
    Lock writeLock = lock.writeLock();
    writeLock.lock();
    try {
      counter++;
    } finally {
      writeLock.unlock();
      System.out.println("end-"+Thread.currentThread().getId());
    }
  }

  public long get() {
    Lock readLock = lock.readLock();
    readLock.lock();
    try {
      return counter;
    } finally {
      readLock.unlock();
    }
  }
}