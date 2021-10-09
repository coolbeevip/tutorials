package com.coolbeevip.java.lock.pessimistic;

/**
 * 悲观锁
 * 适合写操作多的场景，先加锁可以保证写操作时数据正确。
 */
public class SynchronizedCounter {

  static long counter;

  public synchronized long incrementAndGet() {
    return counter++;
  }

  public synchronized long get() {
    return counter;
  }
}