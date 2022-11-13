package com.coolbeevip.groovy;

import java.util.Map;

public interface Evaluator<T> {
  /**
   * 传递实参并执行表达式
   */
  T evaluate(String groovyScriptId, String methodName, Map<String, Object> arguments);

  void loadGroovyScript(String id, String groovyScript) throws InstantiationException, IllegalAccessException;
}
