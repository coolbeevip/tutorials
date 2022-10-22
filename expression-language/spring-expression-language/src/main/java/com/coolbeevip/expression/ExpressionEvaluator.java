package com.coolbeevip.expression;

import java.util.Map;

public interface ExpressionEvaluator<T> {
  /**
   * 表达式返回类型
   */
  void setExpressionType(Class<?> expressionType);

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
