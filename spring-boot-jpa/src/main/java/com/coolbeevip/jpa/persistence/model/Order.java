package com.coolbeevip.jpa.persistence.model;

import com.coolbeevip.jpa.persistence.audit.AuditEntityListener;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author zhanglei
 */
@Slf4j
@EntityListeners(AuditEntityListener.class)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "ORDERS")
public class Order implements Serializable {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name="ID", columnDefinition = "VARCHAR(255)") // 兼容 mysql,pgsql
  private String id;

  @ManyToOne(optional=false)
  @JoinColumn(name="CUSTOMER_ID", referencedColumnName="ID")
  private Customer customer;

  @Basic(optional = false)
  @Column(name="CREATE_AT",updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Date createdAt;

  @Basic(optional = false)
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="LAST_TOUCH_AT")
  private Date lastTouchAt;

  /**
   * 更新前/持久化前处理
   */
  @PreUpdate
  @PrePersist
  public void autoUpdateField() {
    lastTouchAt = new Date();
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
