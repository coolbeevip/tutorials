package com.coolbeevip.design.patterns.structural.decorator;

public abstract class Decorator implements Component {
  protected final Component wrapper;

  public Decorator(Component wrapper) {
    this.wrapper = wrapper;
  }
}
