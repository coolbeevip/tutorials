package com.coolbeevip.cucumber.entities;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhanglei
 */
@Slf4j
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "T_ORDERS")
public class Order implements Serializable {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "ID", columnDefinition = "VARCHAR(255)")
  private String id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "CUSTOMER_ID")
  private Customer customer;

  @Column(name = "PRICE", precision = 9, scale = 2, nullable = false)
  private BigDecimal price;

  @Column(name = "ITEM_NAME", nullable = false)
  private String itemName;

  @Basic(optional = false)
  @Column(name = "CREATED_AT", updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Date createdAt;

  @Basic(optional = false)
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "LAST_UPDATED_AT")
  private Date lastUpdatedAt;

  /**
   * 更新前/持久化前处理
   */
  @PreUpdate
  @PrePersist
  public void autoUpdateField() {
    lastUpdatedAt = new Date();
    if (createdAt == null) {
      createdAt = new Date();
    }
  }
}
