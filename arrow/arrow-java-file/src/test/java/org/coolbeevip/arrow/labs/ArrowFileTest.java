package org.coolbeevip.arrow.labs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;
import org.junit.Test;

public class ArrowFileTest {

  static {
    // compatible JDK 11
    System.setProperty("io.netty.tryReflectionSetAccessible", "true");
  }

  @Test
  public void writerTest() throws Exception {
    String filename = "example.arrow";
    int batchSize = 10;
    int entries = 20;
    FileOutputStream outputStream = new FileOutputStream(new File(filename));
    ArrowWriter writer = new ArrowWriter(outputStream, batchSize, true, Integer.MAX_VALUE);

    //模拟一批数据
    Random random = new Random(System.nanoTime());
    Data data[] = new Data[entries];
    for (int i = 0; i < data.length; i++) {
      data[i] = new Data(random, i);
      long csum = data[i].getSumHash();
    }

    //写入数据
    writer.writeArrow(data);
    writer.closeArrow();
    outputStream.flush();
    outputStream.close();
  }

  @Test
  public void readerTest() throws Exception {
    String filename = "example.arrow";
    FileInputStream inputStream = new FileInputStream(new File(filename));
    ArrowReader reader = new ArrowReader(inputStream, Integer.MAX_VALUE);
  }
}