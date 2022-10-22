package com.coolbeevip.expression;

import java.util.Map;

public interface Evaluator<T> {
  /**
   * 设置表达式
   */
  void setExpression(String expression);

  /**
   * 传递实参并执行表达式
   */
  T evaluate(Map<String, Object> arguments);

  T evaluate();
}
