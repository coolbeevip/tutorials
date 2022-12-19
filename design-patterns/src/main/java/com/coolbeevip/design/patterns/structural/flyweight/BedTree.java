package com.coolbeevip.design.patterns.structural.flyweight;

/**
 * 未使用享元模式的 tree
 */
public class BedTree {
  private final String name;
  private final String color;
  private final byte[] texture;
  private final int x;
  private final int y;

  public BedTree(String name, String color, byte[] texture, int x, int y) {
    this.name = name;
    this.color = color;
    this.texture = texture;
    this.x = x;
    this.y = y;
  }

  public void draw() {
    System.out.println("绘制 " + this.name + " 到 [" + x + "," + y + "]");
  }
}
