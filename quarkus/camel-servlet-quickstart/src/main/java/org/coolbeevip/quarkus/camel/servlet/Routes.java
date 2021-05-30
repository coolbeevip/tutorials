package org.coolbeevip.quarkus.camel.servlet;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

@ApplicationScoped
public class Routes extends RouteBuilder {

  @Override
  public void configure() {

    rest()
        .get("/rest-get")
        .route()
        .setBody(constant("GET: /rest-get"))
        .endRest()
        .post("/rest-post")
        .route()
        .setBody(constant("POST: /rest-post"))
        .endRest();

    from("servlet://hello?matchOnUriPrefix=true")
        .setBody(constant("GET: /hello"));

    from("servlet://custom?servletName=my-named-servlet")
        .setBody(constant("GET: /custom"));

    from("servlet://favorite?servletName=my-favorite-servlet")
        .setBody(constant("GET: /favorite"));

  }

}