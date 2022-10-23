package com.coolbeevip.expression.spel;

import com.coolbeevip.expression.Evaluator;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class SpELExpressionEvaluatorTest {

  @Test
  public void arithmeticTest() {
    Evaluator<Integer> evaluator = new SpELExpressionEvaluator();
    Integer result = evaluator.evaluate("19 + 1");
    assertThat(result, Matchers.is(20));
  }

  @Test
  public void relationalTest() {
    Evaluator<Boolean> evaluator = new SpELExpressionEvaluator();
    Boolean result = evaluator.evaluate("19 > 1");
    assertThat(result, Matchers.is(true));
  }

  @Test
  public void logicalTest() {
    Evaluator<Boolean> evaluator = new SpELExpressionEvaluator();
    Boolean result = evaluator.evaluate("19 > 1 and 19 > 20");
    assertThat(result, Matchers.is(false));
  }

  @Test
  public void conditionalTest() {
    Evaluator<Integer> evaluator = new SpELExpressionEvaluator();
    Integer result = evaluator.evaluate("true ? 1 : 2");
    assertThat(result, Matchers.is(1));
  }

  @Test
  public void regexTest() {
    Evaluator<Boolean> evaluator = new SpELExpressionEvaluator();
    Boolean result = evaluator.evaluate("'100' matches '\\d+'");
    assertThat(result, Matchers.is(true));
  }

  @Test
  public void mapTest() {
    Map<String, Object> params = new HashMap<>();
    params.put("name", "Thomas");
    params.put("age", 35);
    Evaluator<String> evaluator = new SpELExpressionEvaluator();
    String result = evaluator.evaluate("#name", params);
    assertThat(result, Matchers.is(params.get("name")));
  }

  @Test
  public void substringTest() {
    Map<String, Object> params = new HashMap<>();
    params.put("name", "Thomas Zhang");
    Evaluator<String> evaluator = new SpELExpressionEvaluator();
    String result = evaluator.evaluate("#name.substring(0, 6)", params);
    assertThat(result, Matchers.is("Thomas"));
  }

  @Test
  public void concatTest() {
    int total = 10000000;
    long begin = System.currentTimeMillis();
    Evaluator<String> evaluator = new SpELExpressionEvaluator(SpelCompilerMode.IMMEDIATE);

    for (int i = 0; i < total; i++) {
      Map<String, Object> params = new HashMap<>();
      params.put("name", "Thomas");
      params.put("age", 35);
      String result = evaluator.evaluate("#name + '#' + #age", params);
      assertThat(result, Matchers.is(params.get("name").toString() + '#' + params.get("age").toString()));
    }

    long end = System.currentTimeMillis();
    log.info("{} ops/ms", total / (end - begin));
  }

  @Test
  public void ternaryOperatorTest() {
    Evaluator<String> evaluator = new SpELExpressionEvaluator();
    String expressionString = "#full_name != null ? #full_name : #last_name != null ? #last_name : #first_name";

    Map<String, Object> params = new HashMap<>();
    params.put("full_name", null);
    params.put("last_name", null);
    params.put("first_name", "Thomas");
    String result = evaluator.evaluate(expressionString, params);
    assertThat(result, Matchers.is("Thomas"));

    params.clear();
    params.put("full_name", "Thomas Zhang");
    params.put("last_name", null);
    params.put("first_name", "Thomas");
    result = evaluator.evaluate(expressionString, params);
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

    Evaluator<List<Integer>> evaluator = new SpELExpressionEvaluator();
    List<Integer> result = evaluator.evaluate("#myList.?[#this > 1]", params);
    assertThat(result.size(), Matchers.is(2));
  }

  @Test
  public void classTest() {
    Evaluator<Integer> evaluator = new SpELExpressionEvaluator();
    Integer result = evaluator.evaluate("T(Integer).parseInt('1')");
    assertThat(result, Matchers.is(1));
  }

  @Test
  public void staticMethodOfCustomClassTest() {
    Evaluator<String> evaluator = new SpELExpressionEvaluator();
    String result = evaluator.evaluate("T(com.coolbeevip.expression.spel.custom.MyExpression).staticGender(1)");
    assertThat(result, Matchers.is("男"));
  }

  @Test
  public void methodOfCustomClassTest() {
    Evaluator<String> evaluator = new SpELExpressionEvaluator();
    String result = evaluator.evaluate("new com.coolbeevip.expression.spel.custom.MyExpression().gender(0)");
    assertThat(result, Matchers.is("女"));
  }

  @Test
  public void onlyTest() {
    Evaluator<String> evaluator = new SpELExpressionEvaluator();

    int total = 10000000;
    long begin = System.currentTimeMillis();
    for (int i = 0; i < total; i++) {
      Map<String, Object> params = new HashMap<>();
      params.put("name", "Thomas");
      params.put("age", 35);
      String result = params.get("name").toString() + '#' + params.get("age").toString();
    }
    long end = System.currentTimeMillis();
    log.info("{} ops/ms", total / (end - begin));
  }

  @Test
  public void only1Test() {
    Evaluator<String> evaluator = new SpELExpressionEvaluator(SpelCompilerMode.IMMEDIATE);

    int total = 10000000;
    long begin = System.currentTimeMillis();
    for (int i = 0; i < total; i++) {
      Map<String, Object> params = new HashMap<>();
      params.put("name", "Thomas");
      params.put("age", 35);
      String result = evaluator.evaluate("#name + '#' + #age", params);
    }
    long end = System.currentTimeMillis();
    log.info("{} ops/ms", total / (end - begin));
  }

  @Test
  public void only2Test() {
    Evaluator<String> evaluator = new SpELExpressionEvaluator(SpelCompilerMode.OFF);

    int total = 10000000;
    long begin = System.currentTimeMillis();
    for (int i = 0; i < total; i++) {
      Map<String, Object> params = new HashMap<>();
      params.put("name", "Thomas");
      params.put("age", 35);
      String result = evaluator.evaluate("#name + '#' + #age", params);
    }
    long end = System.currentTimeMillis();
    log.info("{} ops/ms", total / (end - begin));
  }

  @Test
  public void only3Test() {
    SpelParserConfiguration configuration = new SpelParserConfiguration
        (SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader());
    ExpressionParser expressionParser  = new SpelExpressionParser(configuration);
    Expression expression = expressionParser.parseExpression("#name + '#' + #age");

    int total = 10000000;
    long begin = System.currentTimeMillis();
    for (int i = 0; i < total; i++) {
      Map<String, Object> params = new HashMap<>();
      params.put("name", "Thomas");
      params.put("age", 35);
      String result = expression.getValue(params,String.class);
    }
    long end = System.currentTimeMillis();
    log.info("{} ops/ms", total / (end - begin));
  }
}
