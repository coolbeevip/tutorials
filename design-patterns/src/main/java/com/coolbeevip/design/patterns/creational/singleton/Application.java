package com.coolbeevip.design.patterns.creational.singleton;

public class Application {
  public static void main(String[] args) {
    Singleton.getInstance().hello();
    Singleton.getInstance().hello();
  }
}
