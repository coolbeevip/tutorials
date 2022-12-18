package com.coolbeevip.design.patterns.creational.builder;

public class Director {
  private final Builder builder;

  public Director(Builder builder) {
    this.builder = builder;
  }

  public void make(String type) {
    this.builder.reset();
    if (type.equals("simple")) {
      this.builder.buildSetupA();
    } else {
      this.builder.buildSetupA();
      this.builder.buildSetupB();
      this.builder.buildSetupC();
    }
  }
}
