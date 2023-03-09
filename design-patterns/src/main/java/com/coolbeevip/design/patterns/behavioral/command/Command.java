package com.coolbeevip.design.patterns.behavioral.command;

public interface Command {
  boolean execute(); // 返回 true 的命令将被放入 history，可以被撤销

  void undo();
}
