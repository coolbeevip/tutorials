package com.coolbeevip.cucumber;


import io.cucumber.java8.En;
import org.hamcrest.Matchers;

import static org.hamcrest.MatcherAssert.assertThat;

public class ShoppingStepDefinitions implements En {
  private int budget = 0;

  public ShoppingStepDefinitions() {
    Given("I have {int} in my wallet", (Integer money) -> budget = money);

    When("I buy {} with {int}", (String any, Integer price) -> budget -= price);

    Then("I should have {int} in my wallet", (Integer finalBudget) ->
        assertThat(budget, Matchers.is(finalBudget.intValue())));
  }

}
