package com.coolbeevip.design.patterns.behavioral.chain;

public class ConcreteHandlerStep3 extends BaseHandler {

  @Override
  protected boolean doHandle(Context context) {
    System.out.println("step 3");
    return true;
  }
}
