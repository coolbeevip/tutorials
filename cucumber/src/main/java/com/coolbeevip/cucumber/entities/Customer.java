package com.coolbeevip.cucumber.entities;

import lombok.*;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
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
@Entity(name = "T_CUSTOMER")
public class Customer implements Serializable {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "ID", columnDefinition = "VARCHAR(255)") // 兼容 mysql,pgsql
  private String id;

  @Column(name = "NAME", length = 50, nullable = false)
  private String name;

  @Column(name = "AMOUNT", nullable = false)
  @Default
  private BigDecimal amount = BigDecimal.valueOf(0);

  /**
   * 一对多关系
   * 一个客户有多个订单
   * <p>
   * 关联对象加载模式 fetch
   * FetchType.LAZY: 懒加载
   * FetchType.EAGER:急加载
   * <p>
   * 级联操作 cascade
   * CascadeType.PERSIST: 级联持久化（保存）操作
   * CascadeType.REMOVE: 级联删除
   * CascadeType.MERGE: 级联更新
   * CascadeType.DETACH: 级联脱离(脱离级联关系)
   * CascadeType.REFRESH: 级联刷新
   * CascadeType.ALL: 以上所有
   */
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
  private Collection<Order> orders;

  /**
   * 自动设置创建时间
   */
  @Basic(optional = false)
  @Column(name = "CREATE_AT", updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Date createdAt;

  /**
   * 自动设置更新时间
   */
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

  /**
   * 删除前处理
   */
  @PreRemove
  public void preRemove() {

  }
}
