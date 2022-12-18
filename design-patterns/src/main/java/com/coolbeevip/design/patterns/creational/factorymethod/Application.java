package com.coolbeevip.design.patterns.creational.factorymethod;

public class Application {
  public static void main(String[] args) {
    Factory creator = new ConcreteFactoryA();
    creator.createProduct().doStuff();

    creator = new ConcreteFactoryB();
    creator.createProduct().doStuff();
  }
}
