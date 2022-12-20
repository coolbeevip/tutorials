package com.coolbeevip.design.patterns.behavioral.chain;

public abstract class BaseHandler implements Handler {
  private Handler next;

  @Override
  public void setNext(Handler handler) {
    this.next = handler;
  }

  @Override
  public void handle(Context context) {
    if (doHandle(context) && this.next != null) {
      this.next.handle(context);
    }
  }

  protected abstract boolean doHandle(Context context);
}
