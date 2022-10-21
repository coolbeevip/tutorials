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

public class ExpressionNumberTest {

  @Test
  @SneakyThrows
  public void addNumberTest() {
    ExpressionParser expressionParser = new SpelExpressionParser();
    Expression expression = expressionParser.parseExpression("19 + 1");
    Integer result = (Integer) expression.getValue();
    assertThat(result, Matchers.is(20));
  }

  @Test
  @SneakyThrows
  public void logicalTest() {
    Map<String, Object> param = new HashMap<>();
    param.put("AGE", 20);
    ExpressionParser expressionParser = new SpelExpressionParser();
    Expression expression = expressionParser.parseExpression("#this['AGE'] > 10 ? #this['AGE'] + 1 : #this['AGE']");
    EvaluationContext context = new StandardEvaluationContext(param);

    Integer result = (Integer) expression.getValue(context);
    assertThat(result, Matchers.is(21));
  }
}
