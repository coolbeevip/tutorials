package com.coolbeevip.design.patterns.creational.abstractfactory.style1;

import com.coolbeevip.design.patterns.creational.abstractfactory.Factory;
import com.coolbeevip.design.patterns.creational.abstractfactory.ProductA;
import com.coolbeevip.design.patterns.creational.abstractfactory.ProductB;

public class ConcreteFactory1 implements Factory {
  @Override
  public ProductA createProductA() {
    return new ConcreteProductA1();
  }

  @Override
  public ProductB createProductB() {
    return new ConcreteProductB1();
  }
}
