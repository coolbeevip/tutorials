package com.coolbeevip.design.patterns.structural.composite;

public class Application {
  public static void main(String[] args) {
    Composite root = new Composite();
    root.add(new Leaf(10));
    root.add(new Leaf(20));

    Composite composite1 = new Composite();
    composite1.add(new Leaf(30));
    Composite composite2 = new Composite();
    composite2.add(new Leaf(30));
    root.add(composite1);
    root.add(composite2);

    System.out.println(root.price());
  }
}
