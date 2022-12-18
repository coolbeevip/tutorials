package com.coolbeevip.design.patterns.creational.abstractfactory;

import com.coolbeevip.design.patterns.creational.abstractfactory.style1.ConcreteFactory1;

public class Application {
  public static void main(String[] args) {
    Client client = new Client(new ConcreteFactory1());
    client.createProductA().doStuff();
    client.createProductB().doStuff();
  }
}
