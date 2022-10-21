package com.coolbeevip.expression.spel;

import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class ExpressionStringTest {

  @Test
  @SneakyThrows
  public void concatTest() {
    ExpressionParser expressionParser = new SpelExpressionParser();
    Expression expression = expressionParser.parseExpression("'Lei' + ' Zhang'");
    String result = (String) expression.getValue();
    assertThat(result, Matchers.is("Lei Zhang"));
  }

  @Test
  @SneakyThrows
  public void javaTest() {
    ExpressionParser expressionParser = new SpelExpressionParser();
    Expression expression = expressionParser.parseExpression("'LeiZhang'.substring(0,3)");
    String result = (String) expression.getValue();
    assertThat(result, Matchers.is("Lei"));
  }

  @Test
  @SneakyThrows
  public void mapTest() {
    Map<String,Object> param = new HashMap<>();
    param.put("LAST_NAME", "Zhang");
    param.put("FIRST_NAME", "Lei");

    ExpressionParser expressionParser = new SpelExpressionParser();
    Expression expression = expressionParser.parseExpression("#this['LAST_NAME']");
    EvaluationContext context = new StandardEvaluationContext(param);
    String result = (String) expression.getValue(context);
    assertThat(result, Matchers.is("Zhang"));

    expression = expressionParser.parseExpression("#this['FIRST_NAME'] + ' ' + #this['LAST_NAME']");
    context = new StandardEvaluationContext(param);
    result = (String) expression.getValue(context);
    assertThat(result, Matchers.is("Lei Zhang"));
  }

  @Test
  @SneakyThrows
  public void chooseOneTest() {
    Map<String, Object> param = new HashMap<>();
    param.put("FULL_NAME", null);
    param.put("NICK_NAME", "COOL");
    param.put("LAST_NAME", "Lei");
    ExpressionParser expressionParser = new SpelExpressionParser();
    Expression expression = expressionParser.parseExpression("#this['FULL_NAME'] !=null ? #this['FULL_NAME'] : #this['NICK_NAME']");
    EvaluationContext context = new StandardEvaluationContext(param);

    String result = (String) expression.getValue(context);
    assertThat(result, Matchers.is("COOL"));
  }

  @Test
  @SneakyThrows
  public void chooseOne2Test() {
    Map<String, Object> param = new HashMap<>();
    param.put("FULL_NAME", null);
    param.put("NICK_NAME", null);
    param.put("LAST_NAME", "Lei");
    ExpressionParser expressionParser = new SpelExpressionParser();
    Expression expression = expressionParser.parseExpression("#this['FULL_NAME'] !=null ? #this['FULL_NAME'] : #this['NICK_NAME'] != null ? #this['NICK_NAME'] : #this['LAST_NAME']");
    EvaluationContext context = new StandardEvaluationContext(param);

    String result = (String) expression.getValue(context);
    assertThat(result, Matchers.is("Lei"));
  }
}
