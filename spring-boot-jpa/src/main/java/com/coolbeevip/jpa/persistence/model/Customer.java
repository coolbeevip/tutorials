package com.coolbeevip.jpa.persistence.model;

import com.coolbeevip.jpa.persistence.audit.AuditEntityListener;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

/**
 * @author zhanglei
 */
@Slf4j
@Entity(name = "CUSTOMER")
@EntityListeners(AuditEntityListener.class)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Customer implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(length = 50, nullable = false)
  private String firstName;

  @Column(length = 50, nullable = false)
  private String lastName;

  @Column(nullable = false)
  @Default
  private Integer age = 0;

  @Basic(optional = false)
  @Column(updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Date createdAt;

  @Basic(optional = false)
  @Temporal(TemporalType.TIMESTAMP)
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
