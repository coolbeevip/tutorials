package com.coolbeevip.design.patterns.structural.decorator;

import java.io.IOException;

public interface Component {
  void writeData(byte[] data) throws IOException;

  byte[] readData() throws IOException;
}
