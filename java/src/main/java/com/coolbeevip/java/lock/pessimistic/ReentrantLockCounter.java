package com.coolbeevip.java.lock.pessimistic;

import com.coolbeevip.java.lock.Counter;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 悲观锁
 * 适合写操作多的场景，先加锁可以保证写操作时数据正确。
 */
public class ReentrantLockCounter implements Counter {

  private ReentrantLock lock = new ReentrantLock();
  private volatile long counter;

  public void increment() {
    lock.lock();
    try{
      counter++;
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