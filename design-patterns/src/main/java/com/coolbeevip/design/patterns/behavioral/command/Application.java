package com.coolbeevip.design.patterns.behavioral.command;

public class Application {
  public static void main(String[] args) {
    Invoker invoker = new Invoker();
    Receiver receiver = new Receiver();

    invoker.executeCommand(new ConcreteCommand1(receiver));
    invoker.undo(); // ConcreteCommand1 不可撤销

    invoker.executeCommand(new ConcreteCommand2(receiver));
    invoker.undo();
  }
}
