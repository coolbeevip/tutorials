package com.coolbeevip.design.patterns.creational.factorymethod;

public class ConcreteCreatorB extends Creator {
  @Override
  public Product createProduct() {
    return new ConcreteProductB();
  }
}
