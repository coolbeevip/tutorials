package org.coolbeevip.quarkus.camel.platformhttp;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

@ApplicationScoped
public class Routes extends RouteBuilder {

  private final Set<Fruit> fruits = Collections.synchronizedSet(new LinkedHashSet<>());
  private final Set<Legume> legumes = Collections.synchronizedSet(new LinkedHashSet<>());

  public Routes() {
    /* Let's add some initial fruits */
    this.fruits.add(new Fruit("Apple", "Winter fruit"));
    this.fruits.add(new Fruit("Pineapple", "Tropical fruit"));

    /* Let's add some initial legumes */
    this.legumes.add(new Legume("Carrot", "Root vegetable, usually orange"));
    this.legumes.add(new Legume("Zucchini", "Summer squash"));
  }

  @Override
  public void configure() throws Exception {
    from("platform-http:/fruits?httpMethodRestrict=GET,POST")
        .choice()
        .when(simple("${header.CamelHttpMethod} == 'GET'"))
        .setBody()
        .constant(fruits)
        .endChoice()
        .when(simple("${header.CamelHttpMethod} == 'POST'"))
        .unmarshal()
        .json(JsonLibrary.Jackson, Fruit.class)
        .process()
        .body(Fruit.class, fruits::add)
        .setBody()
        .constant(fruits)
        .endChoice()
        .end()
        .marshal().json();

    from("platform-http:/legumes?httpMethodRestrict=GET")
        .setBody().constant(legumes)
        .marshal().json();
  }
}
