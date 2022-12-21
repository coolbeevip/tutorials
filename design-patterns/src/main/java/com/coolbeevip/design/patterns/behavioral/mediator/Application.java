package com.coolbeevip.design.patterns.behavioral.mediator;

public class Application {
  public static void main(String[] args) {
    ComponentLogin componentLogin = new ComponentLogin();
    ComponentForm componentForm = new ComponentForm();
    ComponentCode componentCode = new ComponentCode();
    Mediator mediator = new ConcreteMediator();
    mediator.registerComponent(componentLogin);
    mediator.registerComponent(componentForm);
    mediator.registerComponent(componentCode);

    componentForm.setUsername("root");
    componentForm.setPassword("root");
    componentCode.setCode("12345");
    componentLogin.login();

    componentForm.reset();
    componentLogin.login();
  }
}
