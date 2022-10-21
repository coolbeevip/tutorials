package com.coolbeevip.akka.quickstart.protocols.entity;

import java.util.UUID;

/**
 * 访客
 *
 * @author zhanglei
 */
public class Visitor {

  public UUID id;
  public String name;
  public VisitorGender gender;

  public Visitor(UUID id, String name, VisitorGender gender) {
    this.id = id;
    this.name = name;
    this.gender = gender;
  }

  public String getTitle() {
    return String.format("%s %s", this.gender == VisitorGender.male ? "Mr." : "Ms.", this.name);
  }
}
