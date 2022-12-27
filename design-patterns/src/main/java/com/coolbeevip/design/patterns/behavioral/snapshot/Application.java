package com.coolbeevip.design.patterns.behavioral.snapshot;

public class Application {
  public static void main(String[] args) {
    // 创建原发器
    ConcreteOriginator originator = new ConcreteOriginator();

    // 设置原发器状态 1 并保存快照
    originator.setState(1);
    Snapshot snapshot1 = originator.createSnapshot();

    // 设置原发器状态 2 并保存快照
    originator.setState(2);
    Snapshot snapshot2 = originator.createSnapshot();

    // 使用快照 1 恢复
    snapshot1.restore();
    System.out.println(originator.getState()); // 期待 1

    // 使用快照 2 恢复
    snapshot2.restore();
    System.out.println(originator.getState()); // 期待 2
  }
}
