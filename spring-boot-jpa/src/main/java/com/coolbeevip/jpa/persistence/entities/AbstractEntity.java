package com.coolbeevip.jpa.persistence.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

public class AbstractEntity implements Serializable {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "ID", columnDefinition = "VARCHAR(255)") // 兼容 mysql,pgsql
  protected String id;

  /**
   * 自动设置创建时间
   */
  @Basic(optional = false)
  @Column(name = "CREATE_AT", updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  protected Date createdAt;

  /**
   * 自动设置更新时间
   */
  @Basic(optional = false)
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "LAST_UPDATED_AT")
  protected Date lastUpdatedAt;

  /**
   * 更新前/持久化前处理
   */
  @PreUpdate
  @PrePersist
  protected void autoUpdateField() {
    lastUpdatedAt = new Date();
    if (createdAt == null) {
      createdAt = new Date();
    }
  }
}
