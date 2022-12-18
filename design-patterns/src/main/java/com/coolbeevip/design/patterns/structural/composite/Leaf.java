package com.coolbeevip.design.patterns.structural.composite;

public class Leaf implements Component {
  private final int price;

  public Leaf(int price) {
    this.price = price;
  }

  @Override
  public int price() {
    return this.price;
  }
}
