package com.coolbeevip.expression.spel.custom;

public class MyExpression {
  public static String staticGender(Integer v) {
    switch (v) {
      case 0:
        return "女";
      case 1:
        return "男";
      default:
        return "未知";
    }
  }

  public String gender(Integer v) {
    switch (v) {
      case 0:
        return "女";
      case 1:
        return "男";
      default:
        return "未知";
    }
  }
}
