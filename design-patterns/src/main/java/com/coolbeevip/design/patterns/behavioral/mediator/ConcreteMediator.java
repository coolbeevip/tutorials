package com.coolbeevip.design.patterns.behavioral.mediator;

public class ConcreteMediator implements Mediator {
  private ComponentLogin componentLogin;
  private ComponentForm componentForm;
  private ComponentCode componentCode;

  @Override
  public void registerComponent(Component component) {
    component.setMediator(this);
    if (component instanceof ComponentLogin) {
      componentLogin = (ComponentLogin) component;
    } else if (component instanceof ComponentForm) {
      componentForm = (ComponentForm) component;
    } else if (component instanceof ComponentCode) {
      componentCode = (ComponentCode) component;
    }
  }

  @Override
  public void notify(Component component, String message) {
    System.out.println(component.getClass().getSimpleName() + "." + message);
    if (component instanceof ComponentLogin) {
      reactOnLogin();
    } else if (component instanceof ComponentForm) {
      reactOnFormReset();
    } else if (component instanceof ComponentCode) {
      reactOnCodeReset();
    }
  }

  private void reactOnLogin() {
    System.out.println(String.format("登录 name=%s pwd=%s code=%s", componentForm.getUsername(), componentForm.getPassword(), componentCode.getCode()));
  }

  private void reactOnFormReset() {
    componentForm.setPassword(null);
    componentForm.setUsername(null);
    componentCode.setCode(null);
  }

  private void reactOnCodeReset() {
    componentForm.reset();
    componentCode.reset();
  }
}
