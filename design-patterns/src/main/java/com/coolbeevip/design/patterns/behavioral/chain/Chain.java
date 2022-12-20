package com.coolbeevip.design.patterns.behavioral.chain;

public class Chain {
  private BaseHandler head;
  private BaseHandler tail;

  private final Context context;

  public Chain(Context context) {
    this.context = context;
  }

  public void addHandler(BaseHandler handler) {
    handler.setNext(null);
    if (head == null) {
      this.head = handler;
      this.tail = handler;
      return;
    }
    this.tail.setNext(handler);
    this.tail = handler;
  }

  public void handle() {
    if (this.head != null) {
      this.head.handle(this.context);
    }
  }
}
