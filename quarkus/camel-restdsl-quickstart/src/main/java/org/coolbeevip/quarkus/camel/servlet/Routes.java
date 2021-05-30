package org.coolbeevip.quarkus.camel.servlet;

import javax.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class Routes extends RouteBuilder {

  @Override
  public void configure() {
    rest("/say")
        .get("/hello").to("direct:hello")
        .get("/bye").to("direct:bye");

    from("direct:hello")
        .transform().constant("Hello World");
    from("direct:bye")
        .transform().constant("Bye World");
  }

}