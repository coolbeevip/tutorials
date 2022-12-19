package com.coolbeevip.design.patterns.structural.flyweight;

/**
 * 外在状态
 * */
public class Tree {
  private final TreeType type;
  private final int x;
  private final int y;

  public Tree(TreeType type, int x, int y) {
    this.type = type;
    this.x = x;
    this.y = y;
  }

  public void draw() {
    this.type.draw(this.x, this.y);
  }
}
