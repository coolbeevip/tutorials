package com.coolbeevip.design.patterns.creational.factorymethod;

public class ConcreteFactoryA implements Factory {
  @Override
  public Product createProduct() {
    return new ConcreteProductA();
  }
}
