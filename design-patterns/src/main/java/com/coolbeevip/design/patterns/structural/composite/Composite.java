package com.coolbeevip.design.patterns.structural.composite;

import java.util.ArrayList;
import java.util.List;

public class Composite implements Component {
  private List<Component> components = new ArrayList<>();

  @Override
  public int price() {
    return this.components.stream().mapToInt(Component::price).sum();
  }

  public void add(Component component) {
    this.components.add(component);
  }

  public void remove(Component component) {
    this.components.remove(component);
  }

  public List<Component> getChildren() {
    return this.components;
  }
}
