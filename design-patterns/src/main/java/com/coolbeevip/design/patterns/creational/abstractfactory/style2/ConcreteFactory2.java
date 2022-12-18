package com.coolbeevip.design.patterns.creational.abstractfactory.style2;

import com.coolbeevip.design.patterns.creational.abstractfactory.Factory;
import com.coolbeevip.design.patterns.creational.abstractfactory.ProductA;
import com.coolbeevip.design.patterns.creational.abstractfactory.ProductB;

public class ConcreteFactory2 implements Factory {
  @Override
  public ProductA createProductA() {
    return new ConcreteProductA2();
  }

  @Override
  public ProductB createProductB() {
    return new ConcreteProductB2();
  }
}
