package com.coolbeevip.jpa.persistence.audit;

import com.coolbeevip.jpa.persistence.entities.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

/**
 * @author zhanglei
 */
@Slf4j
@Component
public class AuditEntityListener {

  @Autowired(required = false)
  AuditEntityCallback auditCallback;

  @PostPersist
  private void afterPersist(Customer customer) {
    if (auditCallback != null) {
      auditCallback.afterPersist(AuditEventType.CREATED, customer);
    }
  }

  @PostUpdate
  private void afterUpdate(Customer customer) {
    if (auditCallback != null) {
      auditCallback.afterUpdate(AuditEventType.UPDATED, customer);
    }
  }

  @PostRemove
  private void afterRemove(Customer customer) {
    if (auditCallback != null) {
      auditCallback.afterRemove(AuditEventType.DELETED, customer);
    }
  }

  @PostLoad
  private void afterLoad(Customer customer) {
    if (auditCallback != null) {
      auditCallback.afterLoad(AuditEventType.LOAD, customer);
    }
  }
}
