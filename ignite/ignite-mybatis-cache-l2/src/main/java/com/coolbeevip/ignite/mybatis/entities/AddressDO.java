package com.coolbeevip.ignite.mybatis.entities;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class AddressDO {
  String uuid;
  Integer address_level;
  String name;
  String current_name;
  String town_uuid;
  String community_name;
  String building;
  String unit_no;
  String floor_no;
  Integer address_type;
  Integer telecom_village;
  String abbreviation;
  String phoneticize;
  String postcode;
  String description;
  Integer access_user_qty;
  String create_by;
  Timestamp create_time;
  String update_by;
  Timestamp update_time;
  String remark;
  String class_id;
  Double latitude;
  Double longitude;
  String reference_specialty;
  String road_number;
  String villages;
  String road;
  String vilages_alias;
  Integer life_state;
  String canal_id;
  String eight_level_id;
  Integer data_enum;
  Integer data_source;
  String room_no;
  String county_uuid;
  String city_uuid;
  String address_uuid;
  String province_uuid;
  String community_uuid;
  Integer comunity_type;
  String district_uuid;
  String grid_uuid;
  String address_code;
  String sc_hlsf_grid_code;
  Integer region_type;
  String sc_hlsf_grid_uuid;
  Integer zone_type;
}