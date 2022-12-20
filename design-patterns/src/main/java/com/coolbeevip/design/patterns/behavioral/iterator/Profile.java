package com.coolbeevip.design.patterns.behavioral.iterator;

public class Profile {
  private final String name;
  private final String id;

  public Profile(String id, String name) {
    this.name = name;
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }
}
