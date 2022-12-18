package com.coolbeevip.design.patterns.creational.builder;

public class Application {
  public static void main(String[] args) {
    Builder builder = new ConcreteBuilder1();
    Director director = new Director(builder);
    director.make("simple");
    builder.getResult().doStuff();

    builder = new ConcreteBuilder2();
    director = new Director(builder);
    director.make("complex");
    builder.getResult().doStuff();
  }
}
