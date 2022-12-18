package com.coolbeevip.design.patterns.creational.abstractfactory.style1;

import com.coolbeevip.design.patterns.creational.abstractfactory.ProductA;

public class ConcreteProductA1 extends ProductA {
  @Override
  public void doStuff() {
    System.out.println("I'm product A1");
  }
}
