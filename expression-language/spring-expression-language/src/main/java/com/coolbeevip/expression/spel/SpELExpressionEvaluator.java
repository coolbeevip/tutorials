package com.coolbeevip.expression.spel;

import com.coolbeevip.expression.Evaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

public class SpELExpressionEvaluator<T> implements Evaluator<T> {
  private ExpressionParser expressionParser = new SpelExpressionParser();
  private String expression;

  @Override
  public void setExpression(String expression) {
    this.expression = expression;
  }

  @Override
  public T evaluate(Map<String, Object> arguments) {
    Expression expression = expressionParser.parseExpression(this.expression);
    if (arguments == null || arguments.isEmpty()) {
      return (T) expression.getValue();
    } else {
      EvaluationContext context = new StandardEvaluationContext();
      arguments.forEach((k, v) -> {
        context.setVariable(k, v);
      });
      return (T) expression.getValue(context);
    }
  }

  @Override
  public T evaluate() {
    return evaluate(null);
  }
}
