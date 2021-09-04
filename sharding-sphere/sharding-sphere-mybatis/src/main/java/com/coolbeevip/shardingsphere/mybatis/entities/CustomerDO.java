package com.coolbeevip.shardingsphere.mybatis.entities;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhanglei
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDO implements Serializable {

  private String id;
  private String firstName;
  private String lastName;
  private Integer age = 0;
  private Date createdAt;
  private Date lastUpdatedAt;
}
