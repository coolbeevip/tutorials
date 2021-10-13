package com.coolbeevip.java.lock.optimistic.aba;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 正确的账户对象
 */
public class Account {

  // 使用 AtomicStampedReference 定义账户余额，避免 ABA 问题
  private AtomicStampedReference<Integer> balance;
  // 记录交易成功的次数
  private AtomicInteger transactionCount;
  // 记录交易失败的次数
  private ThreadLocal<Integer> currentThreadCASFailureCount;

  public Account() {
    this.balance = new AtomicStampedReference(0,1);
    this.transactionCount = new AtomicInteger(0);
    this.currentThreadCASFailureCount = new ThreadLocal(){
      @Override public Integer initialValue() {
        return 0;
      }
    };
  }

  public int getBalance() {
    return balance.getReference();
  }

  public int getTransactionCount() {
    return transactionCount.get();
  }

  public int getCurrentThreadCASFailureCount() {
    return currentThreadCASFailureCount.get();
  }

  public boolean withdraw(int amount) {
    int current = balance.getReference();
    int stamp = balance.getStamp();
    maybeWait();
    boolean result = balance.compareAndSet(current, current - amount,stamp,stamp+1);
    if (result) {
      transactionCount.incrementAndGet();
    } else {
      int currentCASFailureCount = currentThreadCASFailureCount.get();
      currentThreadCASFailureCount.set(currentCASFailureCount + 1);
    }
    return result;
  }

  private void maybeWait() {
    if ("thread1".equals(Thread.currentThread().getName())) {
      try {
        TimeUnit.SECONDS.sleep(2);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  public boolean deposit(int amount) {
    int current = balance.getReference();
    int stamp = balance.getStamp();
    boolean result = balance.compareAndSet(current, current + amount,stamp,stamp+1);
    if (result) {
      transactionCount.incrementAndGet();
    } else {
      int currentCASFailureCount = currentThreadCASFailureCount.get();
      currentThreadCASFailureCount.set(currentCASFailureCount + 1);
    }
    return result;
  }

}
