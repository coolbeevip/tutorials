package com.coolbeevip.design.patterns.structural.facade;

public class Facade {
  private AdditionalFacade1 facade1 = new AdditionalFacade1(); // 定义附加 Facade 是为了避免出现一个大的上帝 Facade
  private AdditionalFacade2 facade2 = new AdditionalFacade2(); // 定义附加 Facade 是为了避免出现一个大的上帝 Facade

  public void subsystemOperation() {
    System.out.println("此处调用多个子系统方法实现复杂逻辑");
  }

  public void anotherOperation1() {
    facade1.anotherOperation();
  }

  public void anotherOperation2() {
    facade2.anotherOperation();
  }
}
