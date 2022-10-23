package com.coolbeevip.expression.spel;

import com.coolbeevip.expression.Evaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

public class SpELExpressionEvaluator<T> implements Evaluator<T> {
  private final SpelParserConfiguration configuration;
  private final ExpressionParser expressionParser;
  private Map<String, Expression> expressionMap = new HashMap<>();

  public SpELExpressionEvaluator() {
    this(SpelCompilerMode.IMMEDIATE);
  }

  public SpELExpressionEvaluator(SpelCompilerMode mode) {
    this.configuration = new SpelParserConfiguration
        (SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader());
    this.expressionParser = new SpelExpressionParser(configuration);
  }

  @Override
  public T evaluate(String expressionString, Map<String, Object> arguments) {
    Expression expression = this.expressionMap.computeIfAbsent(expressionString, k -> expressionParser.parseExpression(expressionString));
    if (arguments == null || arguments.isEmpty()) {
      return (T) expression.getValue();
    } else {
      EvaluationContext context = new StandardEvaluationContext();
      arguments.entrySet().forEach(entry -> {
        context.setVariable(entry.getKey(), entry.getValue());
      });
      return (T) expression.getValue(context);
    }
  }

  @Override
  public T evaluate(String expressionString) {
    return evaluate(expressionString, null);
  }
}
