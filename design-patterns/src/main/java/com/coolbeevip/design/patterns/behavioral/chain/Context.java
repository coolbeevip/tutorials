package com.coolbeevip.design.patterns.behavioral.chain;

import java.util.Set;

public interface Context {
  void addStep(String value);
  Set<String> getSteps();
}
