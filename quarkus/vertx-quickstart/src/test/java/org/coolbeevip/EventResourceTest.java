package org.coolbeevip;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.startsWith;

@QuarkusTest
public class EventResourceTest {

  @Test
  void testEventBusGreeter() {
    given()
        .when().get("/async/Quarkus")
        .then()
        .statusCode(200)
        .body(startsWith("Hello Quarkus"));
  }
}