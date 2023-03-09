package com.coolbeevip.design.patterns.behavioral.snapshot;

/**
 * 状态拥有者作为原发器
 */
public class ConcreteOriginator implements Originator {
  private int state;

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  @Override
  public Snapshot createSnapshot() {
    return new ConcreteSnapshot(this, this.state);
  }
}
