package com.coolbeevip.design.patterns.creational.factorymethod;

public class ConcreteFactoryB implements Factory {
  @Override
  public Product createProduct() {
    return new ConcreteProductB();
  }
}
