package com.coolbeevip.shardingsphere.mybatis.entities;

import java.io.Serializable;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhanglei
 */
@Builder
@Data
public class CustomerDO implements Serializable {

  private String id;
  private String firstName;
  private String lastName;
  private Integer age = 0;
  private Date createdAt;
  private Date lastUpdatedAt;
}
