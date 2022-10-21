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

public class ExpressionClassTest {

  @Test
  @SneakyThrows
  public void classTest() {
    ExpressionParser expressionParser = new SpelExpressionParser();
    int result = expressionParser.parseExpression("T(Integer).parseInt('1')").getValue(int.class);
    assertThat(result, Matchers.is(1));
  }

  @Test
  @SneakyThrows
  public void useStaticMethodOfCustomClassTest() {
    ExpressionParser expressionParser = new SpelExpressionParser();
    String result = expressionParser.parseExpression("T(com.coolbeevip.expression.spel.MyUtil).gender(1)").getValue(String.class);
    assertThat(result, Matchers.is("男"));
  }

  @Test
  @SneakyThrows
  public void useMethodOfCustomClassTest() {
    ExpressionParser expressionParser = new SpelExpressionParser();
    String result = expressionParser.parseExpression("new com.coolbeevip.expression.spel.MyUtil().gender(0)").getValue(String.class);
    assertThat(result, Matchers.is("女"));
  }
}
