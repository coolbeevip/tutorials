package com.coolbeevip.design.patterns.creational.singleton;

public class Singleton2 {
  private final String param;

  public Singleton2(String param) {
    this.param = param;
  }

  private static class SingletonHolder {
    private static Singleton2 INSTANCE = null;
    private static Singleton2 createInstance(String param) {
      return new Singleton2(param);
    }
  }

  public static Singleton2 getInstance(String param) {
    SingletonHolder.INSTANCE = SingletonHolder.createInstance(param);
    return SingletonHolder.INSTANCE;
  }

  public void hello() {
    System.out.println("hello " + this.hashCode());
  }
}
