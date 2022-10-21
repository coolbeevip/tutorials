package com.coolbeevip.java.lock.pessimistic;

import com.coolbeevip.java.lock.Counter;

/**
 * 悲观锁
 * 适合写操作多的场景，先加锁可以保证写操作时数据正确。
 * <p>
 * 与 Lock 相比的缺陷
 * - 只能作用在方法级别
 * - 不支持公平性（非公平锁）
 * - 会产生阻塞（Lock 提供了 tryLock 方法尝试锁失败）
 * - 等待获取同步块的线程不能被中断（Lock 提供了 lockInterruptably 方法可以中断等待锁的线程）
 */
public class SynchronizedCounter implements Counter {

  private volatile long counter;

  public synchronized void increment() {
    mockTime();
    counter++;
  }

  public synchronized long get() {
    mockTime();
    return counter;
  }
}