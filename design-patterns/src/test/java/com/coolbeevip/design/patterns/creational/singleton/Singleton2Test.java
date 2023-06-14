package com.coolbeevip.design.patterns.creational.singleton;

public class Singleton2Test {

  public static void main(String[] args) {
    Singleton2.getInstance("hello").hello();
    Singleton2.getInstance("world").hello();
  }
}
