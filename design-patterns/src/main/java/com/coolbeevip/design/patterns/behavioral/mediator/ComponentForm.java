package com.coolbeevip.design.patterns.behavioral.mediator;

public class ComponentForm implements Component {
  private Mediator mediator;

  private String username;
  private String password;

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void reset() {
    this.username = null;
    this.password = null;
    this.mediator.notify(this, "reset");
  }

  @Override
  public void setMediator(Mediator mediator) {
    this.mediator = mediator;
  }
}
