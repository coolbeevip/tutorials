package com.coolbeevip.design.patterns.creational.prototype;

public class ConcretePrototype implements Prototype {
  private String field;

  public ConcretePrototype(String field1) {
    this.field = field1;
  }

  public ConcretePrototype(ConcretePrototype prototype) {
    this.field = prototype.field;
  }

  @Override
  public Prototype clone() {
    return new ConcretePrototype(this);
  }

  @Override
  public String getField() {
    return this.field;
  }
}
