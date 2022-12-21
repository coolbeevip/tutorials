package com.coolbeevip.design.patterns.behavioral.mediator;

public class ComponentLogin implements Component {
  private Mediator mediator;

  public void login() {
    this.mediator.notify(this, "login");
  }

  @Override
  public void setMediator(Mediator mediator) {
    this.mediator = mediator;
  }
}
