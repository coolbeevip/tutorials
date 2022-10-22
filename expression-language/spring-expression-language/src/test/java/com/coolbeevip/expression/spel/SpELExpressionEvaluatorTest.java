package com.coolbeevip.expression.spel;

import com.coolbeevip.expression.ExpressionEvaluator;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class SpELExpressionEvaluatorTest {

  @Test
  public void arithmeticTest() {
    ExpressionEvaluator<Integer> evaluator = new SpELExpressionEvaluator();
    evaluator.setExpression("19 + 1");
    Integer result = evaluator.evaluate();
    assertThat(result, Matchers.is(20));
  }

  @Test
  public void relationalTest() {
    ExpressionEvaluator<Boolean> evaluator = new SpELExpressionEvaluator();
    evaluator.setExpression("19 > 1");
    Boolean result = evaluator.evaluate();
    assertThat(result, Matchers.is(true));
  }

  @Test
  public void logicalTest() {
    ExpressionEvaluator<Boolean> evaluator = new SpELExpressionEvaluator();
    evaluator.setExpression("19 > 1 and 19 > 20");
    Boolean result = evaluator.evaluate();
    assertThat(result, Matchers.is(false));
  }

  @Test
  public void conditionalTest() {
    ExpressionEvaluator<Integer> evaluator = new SpELExpressionEvaluator();
    evaluator.setExpression("true ? 1 : 2");
    Integer result = evaluator.evaluate();
    assertThat(result, Matchers.is(1));
  }

  @Test
  public void regexTest() {
    ExpressionEvaluator<Boolean> evaluator = new SpELExpressionEvaluator();
    evaluator.setExpression("'100' matches '\\d+'");
    Boolean result = evaluator.evaluate();
    assertThat(result, Matchers.is(true));
  }

  @Test
  public void mapTest() {
    Map<String, Object> params = new HashMap<>();
    params.put("name", "Thomas");
    params.put("age", 35);
    ExpressionEvaluator<String> evaluator = new SpELExpressionEvaluator();
    evaluator.setExpression("#name");
    String result = evaluator.evaluate(params);
    assertThat(result, Matchers.is(params.get("name")));
  }

  @Test
  public void substringTest() {
    Map<String, Object> params = new HashMap<>();
    params.put("name", "Thomas Zhang");
    ExpressionEvaluator<String> evaluator = new SpELExpressionEvaluator();
    evaluator.setExpression("#name.substring(0, 6)");
    String result = evaluator.evaluate(params);
    assertThat(result, Matchers.is("Thomas"));
  }

  @Test
  public void concatTest() {
    Map<String, Object> params = new HashMap<>();
    params.put("name", "Thomas");
    params.put("age", 35);
    ExpressionEvaluator<String> evaluator = new SpELExpressionEvaluator();
    evaluator.setExpression("#name + '#' + #age");
    String result = evaluator.evaluate(params);
    assertThat(result, Matchers.is(params.get("name").toString() + '#' + params.get("age").toString()));
  }

  @Test
  public void ternaryOperatorTest() {
    ExpressionEvaluator<String> evaluator = new SpELExpressionEvaluator();
    evaluator.setExpression("#full_name != null ? #full_name : #last_name != null ? #last_name : #first_name");

    Map<String, Object> params = new HashMap<>();
    params.put("full_name", null);
    params.put("last_name", null);
    params.put("first_name", "Thomas");
    String result = evaluator.evaluate(params);
    assertThat(result, Matchers.is("Thomas"));

    params.clear();
    params.put("full_name", "Thomas Zhang");
    params.put("last_name", null);
    params.put("first_name", "Thomas");
    result = evaluator.evaluate(params);
    assertThat(result, Matchers.is("Thomas Zhang"));
  }

  @Test
  public void listOperatorTest() {
    List<Integer> values = new ArrayList<>();
    values.add(1);
    values.add(2);
    values.add(3);

    Map<String, Object> params = new HashMap<>();
    params.put("myList", values);

    ExpressionEvaluator<List<Integer>> evaluator = new SpELExpressionEvaluator();
    evaluator.setExpression("#myList.?[#this > 1]");
    List<Integer> result = evaluator.evaluate(params);
    assertThat(result.size(), Matchers.is(2));
  }

  @Test
  public void classTest() {
    ExpressionEvaluator<Integer> evaluator = new SpELExpressionEvaluator();
    evaluator.setExpression("T(Integer).parseInt('1')");
    Integer result = evaluator.evaluate();
    assertThat(result, Matchers.is(1));
  }

  @Test
  public void staticMethodOfCustomClassTest() {
    ExpressionEvaluator<String> evaluator = new SpELExpressionEvaluator();
    evaluator.setExpression("T(com.coolbeevip.expression.spel.custom.MyExpression).staticGender(1)");
    String result = evaluator.evaluate();
    assertThat(result, Matchers.is("男"));
  }

  @Test
  public void methodOfCustomClassTest() {
    ExpressionEvaluator<String> evaluator = new SpELExpressionEvaluator();
    evaluator.setExpression("new com.coolbeevip.expression.spel.custom.MyExpression().gender(0)");
    String result = evaluator.evaluate();
    assertThat(result, Matchers.is("女"));
  }
}
