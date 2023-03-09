package com.coolbeevip.design.patterns.creational.builder;

import com.coolbeevip.design.patterns.creational.factorymethod.Product;

public interface Builder {
  void reset();

  void buildSetupA();

  void buildSetupB();

  void buildSetupC();

  Product getResult();
}
