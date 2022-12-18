package com.coolbeevip.design.patterns.creational.factorymethod;

public class Application {
  public static void main(String[] args) {
    Creator creator = new ConcreteCreatorA();
    creator.createProduct().doStuff();

    creator = new ConcreteCreatorB();
    creator.createProduct().doStuff();
  }
}
