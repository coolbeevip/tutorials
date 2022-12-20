package com.coolbeevip.design.patterns.behavioral.chain;

public class ConcreteHandlerStep1 extends BaseHandler {

  @Override
  protected boolean doHandle(Context context) {
    System.out.println("step 1");
    return true;
  }
}
