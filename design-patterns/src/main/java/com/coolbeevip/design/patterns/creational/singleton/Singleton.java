package com.coolbeevip.design.patterns.creational.singleton;

public class Singleton {
  private static volatile Singleton instance; // must volatile

  // Double-Checked Locking with Singleton
  public static Singleton getInstance() {
    if (instance == null) {
      synchronized (Singleton.class) {
        if (instance == null) {
          instance = new Singleton();
        }
      }
    }
    return instance;
  }

  public void hello() {
    System.out.println("hello " + this.hashCode());
  }
}
