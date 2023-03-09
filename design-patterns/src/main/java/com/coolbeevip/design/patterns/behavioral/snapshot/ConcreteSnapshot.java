package com.coolbeevip.design.patterns.behavioral.snapshot;

/**
 * 状态具体备忘录
 */
public class ConcreteSnapshot implements Snapshot {
  private final ConcreteOriginator originator;
  private final int state;

  public ConcreteSnapshot(ConcreteOriginator originator, int state) {
    this.originator = originator;
    this.state = state;
  }

  @Override
  public void restore() {
    this.originator.setState(this.state);
  }
}
