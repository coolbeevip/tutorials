package org.coolbeevip;


import io.quarkus.vertx.ConsumeEvent;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventConsumer {

  @ConsumeEvent("greeting")
  public String greeting(String name) {
    return "Hello " + name;
  }
}