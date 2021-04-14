package com.coolbeevip.akka.quickstart.protocols;

/**
 * 访客
 * @author zhanglei
 */
public class Visitor {
  public String name;
  public VisitorGender gender;

  public Visitor(String name, VisitorGender gender) {
    this.name = name;
    this.gender = gender;
  }
}
