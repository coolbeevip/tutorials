package org.coolbeevip.quarkus.camel.platformhttp;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

@QuarkusTest
public class RoutesTest {


    @Test
    public void fruits() {

        /* Assert the initial fruits are there */
        given()
            .when().get("/fruits")
            .then()
            .statusCode(200)
            .body(
                "$.size()", is(2),
                "name", containsInAnyOrder("Apple", "Pineapple"),
                "description", containsInAnyOrder("Winter fruit", "Tropical fruit")
            );

        /* Add a new fruit */
        given()
            .body("{\"name\": \"Pear\", \"description\": \"Winter fruit\"}")
            .header("Content-Type", "application/json")
            .when()
            .post("/fruits")
            .then()
            .statusCode(200)
            .body(
                "$.size()", is(3),
                "name", containsInAnyOrder("Apple", "Pineapple", "Pear"),
                "description", containsInAnyOrder("Winter fruit", "Tropical fruit", "Winter fruit")
            );
    }

    @Test
    public void legumes() {
        given()
            .when().get("/legumes")
            .then()
            .statusCode(200)
            .body("$.size()", is(2),
                "name", containsInAnyOrder("Carrot", "Zucchini"),
                "description", containsInAnyOrder("Root vegetable, usually orange", "Summer squash"));
    }
}
