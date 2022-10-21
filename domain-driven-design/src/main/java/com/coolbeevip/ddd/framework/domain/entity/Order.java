package com.coolbeevip.ddd.framework.domain.entity;

import com.sun.media.sound.InvalidDataException;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 订单
 *
 * @author zhanglei
 */
public class Order {

  private String orderId;
  private String userId;
  private String orderNo;
  private Date orderDate;
  private Status status;
  private Set<Item> items;
  private ShippingAddress shippingAddress;
  private Payment payment;
  private MoneytoryValue moneytoryValue;

  public Order(String userId) throws InvalidDataException {
    this.setUserId(userId);
    this.generateOrderNo();
    this.initCart();
    this.markPaymentExepected();
    this.moneytoryValue();
  }

  private void setUserId(String userId) throws InvalidDataException {
    if (userId == null || userId.length() == 0) {
      throw new InvalidDataException("Invalid UserId");
    } else {
      this.userId = userId;
    }
  }

  public void markPaymentExepected() {
    this.status = Status.PAYMENT_EXPECTED;
  }

  private void generateOrderNo() {
    this.orderDate = new Date(Calendar.getInstance().getTimeInMillis());
    this.orderNo = "OD" + orderDate.getTime() + "";
  }

  private void initCart() {
    this.items = new HashSet<Item>();
  }

  public MoneytoryValue moneytoryValue() {
    moneytoryValue = new MoneytoryValue(getTotal(), "USD");
    return moneytoryValue;
  }

  public void addItem(String itemId, String itemName, Double price, Integer quantity)
      throws InvalidDataException {
    Item item = new Item(itemId, itemName, price, quantity, this);
    this.items.add(item);
    this.moneytoryValue();
  }

  public Double getTotal() {
    double total = 0.0;
    for (Iterator iterator = items.iterator(); iterator.hasNext(); ) {
      Item item = (Item) iterator.next();
      System.out.println(
          "---" + item.getItemId() + "--" + item.getItemName() + "--" + item.getPrice() + "--"
              + item
              .getQuantity());
      total = total + item.getSubTotal();
    }
    return total;
  }

  public static enum Status {

    /**
     * 已初始化，但是未付款，依旧可以修改
     */
    PAYMENT_EXPECTED,

    /**
     * 已付款，不允许任何修改
     */
    PAID,

    /**
     * 处理中.
     */
    PREPARING,

    /**
     * 已就绪，等待用户选货
     */
    READY,

    /**
     * 已完成
     */
    TAKEN;
  }
}
