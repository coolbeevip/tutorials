package com.coolbeevip.expression.janino;

import com.coolbeevip.expression.Evaluator;
import org.codehaus.janino.ExpressionEvaluator;

import java.util.Iterator;
import java.util.Map;

public class JaninoExpressionEvaluator<T> implements Evaluator<T> {
  //private ExpressionEvaluator expressionParser = new ExpressionEvaluator();
  private String expression;

  @Override
  public void setExpression(String expression) {
    this.expression = expression;
  }

  @Override
  public T evaluate(Map<String, Object> arguments) {
    ExpressionEvaluator expressionParser = new ExpressionEvaluator();
    try {
      if (arguments == null || arguments.isEmpty()) {
        expressionParser.cook(this.expression);
        return (T) expressionParser.evaluate(null);
      } else {
        String[] names = new String[arguments.size()];
        Class[] classes = new Class[arguments.size()];
        Object[] values = new Object[arguments.size()];
        int index = 0;
        Iterator<String> it = arguments.keySet().iterator();
        while (it.hasNext()) {
          String name = it.next();
          names[index] = name;
          values[index] = arguments.get(name);
          classes[index] = arguments.get(name) != null ? arguments.get(name).getClass() : Object.class;
          index++;
        }
        expressionParser.setParameters(names, classes);
        expressionParser.cook(this.expression);
        return (T) expressionParser.evaluate(values);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public T evaluate() {
    return evaluate(null);
  }
}
