package com.coolbeevip.design.patterns.creational.abstractfactory;

public class Client {
  private final Factory factory;

  public Client(Factory factory) {
    this.factory = factory;
  }

  public ProductA createProductA() {
    return this.factory.createProductA();
  }

  public ProductB createProductB() {
    return this.factory.createProductB();
  }
}
