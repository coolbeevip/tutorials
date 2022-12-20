package com.coolbeevip.design.patterns.behavioral.command;

public class ConcreteCommand2 implements Command {
  private final Receiver receiver;

  public ConcreteCommand2(Receiver receiver) {
    this.receiver = receiver;
  }

  @Override
  public boolean execute() {
    receiver.receiver("执行命令 2");
    return true;
  }

  @Override
  public void undo() {
    receiver.receiver("撤销命令 2");
  }
}
