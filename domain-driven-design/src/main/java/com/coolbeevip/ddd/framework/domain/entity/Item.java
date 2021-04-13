package com.coolbeevip.ddd.framework.domain.entity;

/**
 订单项目
 */
public class Item {

  private String id;
  private Order order;
  private String itemId;
  private String itemName;
  private Double price;
  private Integer quantity;
  private Double subTotal;

  public String getItemId() {
    return itemId;
  }

  public String getItemName() {
    return itemName;
  }

  public Double getPrice() {
    return price;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public Double getSubTotal() {
    return subTotal;
  }

  public Item(String itemId, String itemName, Double price, Integer quantity, Order ordder) {
    this.itemId = itemId;
    this.itemName = itemName;
    this.price = price;
    this.quantity = quantity;
    this.order = ordder;
  }
}
