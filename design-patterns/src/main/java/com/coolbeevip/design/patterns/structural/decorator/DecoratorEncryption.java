package com.coolbeevip.design.patterns.structural.decorator;

import java.io.IOException;
import java.util.Base64;

public class DecoratorEncryption extends Decorator {
  public DecoratorEncryption(Component wrapper) {
    super(wrapper);
  }

  @Override
  public void writeData(byte[] data) throws IOException {
    wrapper.writeData(Base64.getEncoder().encode(data));
  }

  @Override
  public byte[] readData() throws IOException {
    return Base64.getDecoder().decode(wrapper.readData());
  }
}
