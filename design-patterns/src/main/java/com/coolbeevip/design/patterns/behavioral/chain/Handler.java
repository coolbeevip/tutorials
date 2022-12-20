package com.coolbeevip.design.patterns.behavioral.chain;

public interface Handler {
  void setNext(Handler handler);

  void handle(Context context);
}
