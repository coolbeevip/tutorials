package com.coolbeevip.expression;

import java.util.Map;

public interface Evaluator<T> {
  /**
   * 传递实参并执行表达式
   */
  T evaluate(String expressionString, Map<String, Object> arguments);

  T evaluate(String expressionString);
}
