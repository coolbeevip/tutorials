package com.coolbeevip.java.comparator;


import java.util.Comparator;

public class StudentMultiFields implements Comparable<StudentMultiFields> {
  private int age;
  private int high;

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public int getHigh() {
    return high;
  }

  public void setHigh(int high) {
    this.high = high;
  }

  @Override
  public String toString() {
    return "StudentMultiFields{" +
        "age=" + age +
        ", high=" + high +
        '}';
  }

  @Override
  public int compareTo(StudentMultiFields o) {
    return Comparator.comparing(StudentMultiFields::getAge)
        .thenComparing(StudentMultiFields::getHigh)
        .reversed()
        .compare(this, o);
  }
}
