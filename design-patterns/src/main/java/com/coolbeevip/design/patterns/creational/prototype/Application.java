package com.coolbeevip.design.patterns.creational.prototype;

public class Application {
  public static void main(String[] args) {
    Prototype prototype = new ConcretePrototype("hello");
    System.out.println(prototype.getField());
    Prototype prototypeClone = prototype.clone();
    System.out.println(prototypeClone.getField());
  }
}
