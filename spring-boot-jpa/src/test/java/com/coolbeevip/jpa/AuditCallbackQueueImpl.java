package com.coolbeevip.jpa;

import com.coolbeevip.jpa.persistence.audit.AuditEntityCallback;
import com.coolbeevip.jpa.persistence.audit.AuditEvent;
import com.coolbeevip.jpa.persistence.audit.AuditEventType;
import java.util.LinkedList;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhanglei
 */
@Slf4j
public class AuditCallbackQueueImpl implements AuditEntityCallback {

  LinkedList<AuditEvent> auditRecords = new LinkedList();

  @Override
  public void afterPersist(AuditEventType type, Object o) {
    auditRecords.add(AuditEvent.builder().type(type).data(o).build());
    log.trace("[AUDIT-{}] : {}", type, o);
  }

  @Override
  public void afterUpdate(AuditEventType type, Object o) {
    auditRecords.add(AuditEvent.builder().type(type).data(o).build());
    log.trace("[AUDIT-{}] : {}", type, o);
  }

  @Override
  public void afterRemove(AuditEventType type, Object o) {
    auditRecords.add(AuditEvent.builder().type(type).data(o).build());
    log.trace("[AUDIT-{}] : {}", type, o);
  }

  @Override
  public void afterLoad(AuditEventType type, Object o) {
    auditRecords.add(AuditEvent.builder().type(type).data(o).build());
    log.trace("[AUDIT-{}] : {}", type, o);
  }

  public LinkedList<AuditEvent> getAuditRecords() {
    return auditRecords;
  }
}
