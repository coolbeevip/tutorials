package com.coolbeevip.design.patterns.behavioral.mediator;

public interface Mediator {
  void registerComponent(Component component);
  void notify(Component component, String message);
}
