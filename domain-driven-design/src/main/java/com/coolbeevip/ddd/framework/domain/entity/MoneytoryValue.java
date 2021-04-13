package com.coolbeevip.ddd.framework.domain.entity;

public class MoneytoryValue {

  private Double total;
  private String unit;

  public MoneytoryValue(Double total, String unit) {
    this.total = total;
    this.unit = unit;
  }
}
