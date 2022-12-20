package com.coolbeevip.design.patterns.behavioral.iterator;

public class Application {
  public static void main(String[] args) {
    ProfileIterator iterator = new ConcreteProfileIterator();
    while (iterator.hasNext()) {
      Profile profile = iterator.next();
      System.out.println("id:" + profile.getId() + " name:" + profile.getName());
    }
  }
}
