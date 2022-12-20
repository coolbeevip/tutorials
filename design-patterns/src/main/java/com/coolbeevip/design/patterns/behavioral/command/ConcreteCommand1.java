package com.coolbeevip.design.patterns.behavioral.command;

public class ConcreteCommand1 implements Command {
  private final Receiver receiver;

  public ConcreteCommand1(Receiver receiver) {
    this.receiver = receiver;
  }

  @Override
  public boolean execute() {
    receiver.receiver("执行命令 1");
    return false;
  }

  @Override
  public void undo() {
    receiver.receiver("撤销命令 1");
  }
}
