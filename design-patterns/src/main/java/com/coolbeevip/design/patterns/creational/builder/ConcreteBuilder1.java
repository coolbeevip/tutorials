package com.coolbeevip.design.patterns.creational.builder;

import com.coolbeevip.design.patterns.creational.factorymethod.Product;

import java.util.ArrayList;
import java.util.List;

public class ConcreteBuilder1 implements Builder {
  private List<String> components = new ArrayList<>();

  @Override
  public void reset() {
    components.clear();
  }

  @Override
  public void buildSetupA() {
    components.add("A");
  }

  @Override
  public void buildSetupB() {
    components.add("B");
  }

  @Override
  public void buildSetupC() {
    components.add("C");
  }

  @Override
  public Product getResult() {
    return new ConcreteProduct1(components);
  }


}
