package hellocucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class StepDefinitions {

  static String isItFriday(String today) {
    return "Nope";
  }

  private String today;
  private String actualAnswer;
  @Given("today is Sunday")
  public void today_is_sunday() {
    today = "Sunday";
  }

  @When("I ask whether it's Friday yet")
  public void i_ask_whether_it_s_friday_yet() {
    actualAnswer = isItFriday(today);
  }

  @Then("I should be told {string}")
  public void i_should_be_told(String expectedAnswer) {
    assertThat(expectedAnswer, is(actualAnswer));
  }

}
