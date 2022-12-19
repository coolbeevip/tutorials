package com.coolbeevip.design.patterns.structural.decorator;


public class ConcreteComponent implements Component {
  private byte[] data;

  @Override
  public void writeData(byte[] data) {
    this.data = data;
  }

  @Override
  public byte[] readData() {
    return this.data;
  }
}
