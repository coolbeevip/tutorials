package com.coolbeevip.design.patterns.behavioral.command;

import java.util.Stack;

public class CommandHistory {
  private Stack<Command> history = new Stack<>();

  public void push(Command command) {
    this.history.push(command);
  }

  public Command pop() {
    return history.isEmpty() ? null : this.history.pop();
  }

  public boolean isEmpty() {
    return history.isEmpty();
  }
}
