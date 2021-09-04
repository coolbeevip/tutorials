package com.coolbeevip.shardingsphere.mybatis.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class OrderDO implements Serializable {

  private String id;
  private String customerId;
  private BigDecimal totalPrice;
  private String orderDesc;
  private Date createdAt;
  private Date lastUpdatedAt;
}
