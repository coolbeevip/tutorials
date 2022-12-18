package com.coolbeevip.design.patterns.structural.adapter;

public class Service {
  // 作为被适配的第三方接口，我们不能也无法改变接口以及实现
  public String getMsg() {
    return "I'm a message";
  }
}
