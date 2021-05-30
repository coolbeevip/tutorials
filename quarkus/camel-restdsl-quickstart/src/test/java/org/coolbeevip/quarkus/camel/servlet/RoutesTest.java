package org.coolbeevip.quarkus.camel.servlet;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class RoutesTest {

  @Test
  public void multiplePaths() {
    RestAssured.when().get("/say/hello").then().body(IsEqual.equalTo("Hello World"));
    RestAssured.when().get("/say/bye").then().body(IsEqual.equalTo("Bye World"));
  }
}
