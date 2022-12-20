package com.coolbeevip.design.patterns.behavioral.chain;

public class Application {
  public static void main(String[] args) {
    Chain chain = new Chain(new ConcreteContext());
    chain.addHandler(new ConcreteHandlerStep1());
    chain.addHandler(new ConcreteHandlerStep2());
    chain.addHandler(new ConcreteHandlerStep3());
    chain.handle();
  }
}
