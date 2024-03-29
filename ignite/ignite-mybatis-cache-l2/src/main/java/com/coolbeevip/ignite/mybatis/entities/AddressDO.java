package com.coolbeevip.ignite.mybatis.entities;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddressDO {
  String uuid;
  String name;
  Double latitude;
  Double longitude;
  String country;
  String city;
  String postcode;
  String remark;
}