package com.coolbeevip.design.patterns.behavioral.chain;

public class ConcreteHandlerStep2 extends BaseHandler {

  @Override
  protected boolean doHandle(Context context) {
    System.out.println("step 2");
    return true;
  }
}
