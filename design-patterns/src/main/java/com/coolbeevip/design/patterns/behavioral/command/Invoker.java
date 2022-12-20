package com.coolbeevip.design.patterns.behavioral.command;

/**
 * 发送者
 */
public class Invoker {
  private CommandHistory history = new CommandHistory();


  public void executeCommand(Command command) {
    if (command.execute()) {
      history.push(command);
    }
  }

  public void undo() {
    Command command = this.history.pop();
    if (command != null) {
      command.undo();
    } else {
      System.out.println("没有找到可撤销的命令");
    }
  }
}
