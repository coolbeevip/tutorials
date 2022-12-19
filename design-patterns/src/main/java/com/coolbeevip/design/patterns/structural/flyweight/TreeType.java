package com.coolbeevip.design.patterns.structural.flyweight;

/**
 * 内在状态（享元对象）
 * */
public class TreeType {
  private final String name;
  private final String color;
  private final byte[] texture;

  public TreeType(String name, String color, byte[] texture) {
    this.name = name;
    this.color = color;
    this.texture = texture;
  }

  public String getName() {
    return name;
  }

  public String getColor() {
    return color;
  }

  public byte[] getTexture() {
    return texture;
  }

  public void draw(int x, int y) {
    System.out.println("绘制 " + this.name + " 到 [" + x + "," + y + "]");
  }
}
