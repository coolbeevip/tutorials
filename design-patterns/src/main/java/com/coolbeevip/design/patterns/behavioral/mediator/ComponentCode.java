package com.coolbeevip.design.patterns.behavioral.mediator;

public class ComponentCode implements Component {
  private String code;
  private Mediator mediator;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void reset() {
    this.mediator.notify(this, "reset");
  }

  @Override
  public void setMediator(Mediator mediator) {
    this.mediator = mediator;
  }
}
