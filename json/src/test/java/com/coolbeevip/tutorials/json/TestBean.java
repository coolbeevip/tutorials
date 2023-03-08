package com.coolbeevip.tutorials.json;

public class TestBean {
  private final String name;
  private final Integer age;

  public TestBean(String name, Integer age) {
    this.name = name;
    this.age = age;
  }

  public String getName() {
    return name;
  }

  public Integer getAge() {
    return age;
  }
}
