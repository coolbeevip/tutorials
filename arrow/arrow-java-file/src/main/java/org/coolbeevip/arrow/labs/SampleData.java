package org.coolbeevip.arrow.labs;

import java.util.Random;
import org.coolbeevip.arrow.annotation.ArrowField;
import org.coolbeevip.arrow.annotation.ArrowSchema;

@ArrowSchema
public class SampleData implements IData {

  @ArrowField(index = 0)
  public int anInt;
  @ArrowField(index = 1)
  public long aLong;
  @ArrowField(index = 2)
  public byte[] arr;
  @ArrowField(index = 3)
  public float aFloat;
  @ArrowField(index = 4)
  public double aDouble;
  @ArrowField(index = 5)
  public String aString;

  public Random random;

  public SampleData(Random random, int index) {
    this.random = random;
    this.anInt = this.random.nextInt(1024);
    this.aLong = this.random.nextInt(Integer.MAX_VALUE);
    this.arr = new byte[this.random.nextInt(1024)];
    this.random.nextBytes(this.arr);
    this.aFloat = this.random.nextFloat();
    this.aDouble = this.random.nextDouble();
    this.aString = "name-" + this.anInt;
  }

  public int getAnInt() {
    return anInt;
  }

  public void setAnInt(int anInt) {
    this.anInt = anInt;
  }

  public long getaLong() {
    return aLong;
  }

  public void setaLong(long aLong) {
    this.aLong = aLong;
  }

  public byte[] getArr() {
    return arr;
  }

  public void setArr(byte[] arr) {
    this.arr = arr;
  }

  public float getaFloat() {
    return aFloat;
  }

  public void setaFloat(float aFloat) {
    this.aFloat = aFloat;
  }

  public double getaDouble() {
    return aDouble;
  }

  public void setaDouble(double aDouble) {
    this.aDouble = aDouble;
  }

  public String getaString() {
    return aString;
  }

  public void setaString(String aString) {
    this.aString = aString;
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
    ret += SampleData.hashArray(this.arr);
    ret += aFloat;
    ret += aDouble;
    ret += aString.hashCode();
    return ret;
  }

  @Override
  public void setValue(int index, Object value) {

  }

  @Override
  public Object getValue(int index) {
    switch (index) {
      case 0:
        return this.anInt;
      case 1:
        return this.aLong;
      case 2:
        return this.arr;
      case 3:
        return this.aFloat;
      case 4:
        return this.aDouble;
      case 5:
        return this.aString;
      default:
        return null;
    }
  }
}