package com.coolbeevip.ignite.mybatis.entities;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CountryDO {
  String uuid;
  String name;
  Double capital;
  Double code;
}