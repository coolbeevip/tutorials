package com.coolbeevip.jpa.persistence.audit;

/**
 * @author zhanglei
 */
public interface AuditEntityCallback<T> {

  void afterPersist(AuditEventType event, T t);

  void afterUpdate(AuditEventType event, T t);

  void afterRemove(AuditEventType event, T t);

  void afterLoad(AuditEventType event, T t);
}
