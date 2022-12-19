package com.coolbeevip.design.patterns.structural.facade;

import java.io.IOException;

public class Application {
  public static void main(String[] args) throws IOException {
    Facade facade = new Facade();
    facade.subsystemOperation();
    facade.anotherOperation1();
    facade.anotherOperation2();
  }
}
