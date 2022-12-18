package com.coolbeevip.design.patterns.creational.builder;

import com.coolbeevip.design.patterns.creational.factorymethod.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ConcreteProduct1 implements Product {
  private final List<String> components;

  public ConcreteProduct1(List<String> components) {
    this.components = components;
  }

  @Override
  public void doStuff() {
    System.out.println("I'm product1 include " +components.stream().collect(Collectors.joining(",")));
  }
}
