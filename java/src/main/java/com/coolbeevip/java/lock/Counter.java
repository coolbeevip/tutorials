package com.coolbeevip.java.lock;

public interface Counter {

  void increment();

  long get();

  /**
   * 锁定方法块中需要模拟一个块执行时间，否则锁耗时可能都要大于方法耗时
   */
  default void mockTime() {
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
