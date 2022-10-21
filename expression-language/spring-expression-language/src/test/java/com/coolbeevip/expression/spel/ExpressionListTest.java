package com.coolbeevip.expression.spel;

import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class ExpressionListTest {

  @Test
  @SneakyThrows
  public void listTest() {
    List values = new ArrayList<>();
    values.add(null);
    values.add(2);
    values.add(1);

    ExpressionParser expressionParser = new SpelExpressionParser();
    EvaluationContext context = new StandardEvaluationContext(values);
    Expression expression = expressionParser.parseExpression("#this.?[#this != null]");

    List result = expression.getValue(context, List.class);
    assertThat(result.size(), Matchers.is(2));
  }
}
