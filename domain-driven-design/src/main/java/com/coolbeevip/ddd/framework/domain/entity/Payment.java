package com.coolbeevip.ddd.framework.domain.entity;

import java.util.Calendar;

/**
 * 订单付款明细
 */
public class Payment {

  private String id;
  private String mode;
  private String transactionId;
  private Double amount;
  private Order order;

  private void generateTransactionId() {
    this.transactionId = "TR" + this.mode.trim() + "" + Calendar.getInstance().getTimeInMillis();
  }
}
