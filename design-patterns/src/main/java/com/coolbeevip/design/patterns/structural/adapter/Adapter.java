package com.coolbeevip.design.patterns.structural.adapter;

public class Adapter implements Client {
  private final Service service;

  public Adapter(Service service) {
    this.service = service;
  }

  @Override
  public String getMessage() {
    return this.service.getMsg();
  }
}
