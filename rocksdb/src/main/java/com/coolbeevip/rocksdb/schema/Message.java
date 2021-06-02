package com.coolbeevip.rocksdb.schema;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author zhanglei
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

  private String uuid;
  private Timestamp time;
  private String f1;
  private String f2;
  private String f3;
  private String f4;
  private String f5;
  private String f6;
  private String f7;
  private String f8;
  private String f9;
  private String f10;
  private Integer f11;
}