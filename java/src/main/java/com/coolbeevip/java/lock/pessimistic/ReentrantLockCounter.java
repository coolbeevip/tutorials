package com.coolbeevip.java.lock.pessimistic;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 悲观锁
 * 适合写操作多的场景，先加锁可以保证写操作时数据正确。
 */
public class ReentrantLockCounter {

  private ReentrantLock lock = new ReentrantLock();
  private volatile long counter;

  public long incrementAndGet() {
    lock.lock();
    try{
      return counter++;
    }finally {
      lock.unlock();
    }
  }

  public long get() {
    lock.lock();
    try{
      return counter;
    }finally {
      lock.unlock();
    }
  }
}