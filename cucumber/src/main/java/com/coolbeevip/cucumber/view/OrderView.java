package com.coolbeevip.cucumber.view;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderView {
  private String customerId;
  private BigDecimal price;
  private String itemName;
}
