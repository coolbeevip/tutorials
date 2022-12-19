package com.coolbeevip.design.patterns.structural.decorator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DecoratorCompression extends Decorator {
  public DecoratorCompression(Component wrapper) {
    super(wrapper);
  }

  @Override
  public void writeData(byte[] data) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
      gzip.write(data);
    }
    this.wrapper.writeData(out.toByteArray());
  }

  @Override
  public byte[] readData() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayInputStream in = new ByteArrayInputStream(this.wrapper.readData());
    try (GZIPInputStream gzip = new GZIPInputStream(in)) {
      byte[] buffer = new byte[256];
      int n;
      while ((n = gzip.read(buffer)) >= 0) {
        out.write(buffer, 0, n);
      }
      return out.toByteArray();
    }
  }
}
