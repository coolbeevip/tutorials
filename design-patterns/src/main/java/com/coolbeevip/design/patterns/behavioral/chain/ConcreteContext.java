package com.coolbeevip.design.patterns.behavioral.chain;

import java.util.HashSet;
import java.util.Set;

public class ConcreteContext implements Context {
  Set<String> steps = new HashSet<>();

  @Override
  public void addStep(String value) {
    this.steps.add(value);
  }

  @Override
  public Set<String> getSteps() {
    return this.steps;
  }
}
