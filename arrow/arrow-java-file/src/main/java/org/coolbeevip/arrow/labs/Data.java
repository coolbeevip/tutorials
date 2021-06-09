package org.coolbeevip.arrow.labs;

import java.util.Random;

public class Data {

  public int anInt;
  public long aLong;
  public byte[] arr;
  public float aFloat;
  public double aDouble;
  public String aString;
  public Random random;

  public Data(Random random, int index) {
    this.random = random;
    this.anInt = this.random.nextInt(1024);
    this.aLong = this.random.nextInt(Integer.MAX_VALUE);
    this.arr = new byte[this.random.nextInt(1024)];
    this.random.nextBytes(this.arr);
    this.aFloat = this.random.nextFloat();
    this.aDouble = this.random.nextDouble();
    this.aString = "name-" + this.anInt;
  }

  public static String firstX(byte[] data, int items) {
    int toProcess = Math.min(items, data.length);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < toProcess; i++) {
      sb.append(String.format("0x%02x", data[i]) + " ");
    }
    return sb.toString();
  }

  public static long hashArray(byte[] data) {
    long ret = 0;
    for (int i = 0; i < data.length; i++) {
      ret += data[i];
    }
    return ret;
  }

  @Override
  public String toString() {
    return anInt + "\t | " +
        +aLong + "\t | " +
        " arr[" + this.arr.length + "] " + firstX(this.arr, 5) + "\t | " +
        +aFloat + " \t | "
        + aDouble + " \t | "
        + aString;
  }

  public long getSumHash() {
    long ret = 0;
    ret += anInt;
    ret += aLong;
    ret += Data.hashArray(this.arr);
    ret += aFloat;
    ret += aDouble;
    ret += aString.hashCode();
    return ret;
  }
}