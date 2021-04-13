package com.coolbeevip.ddd.framework.domain.entity;

/**
 订单收获地址
 */
public class ShippingAddress {

  private String id;
  private String label;
  private String address;
  private String country;
  private String province;
  private String city;
  private String postalcode;
  private Order order;
}
