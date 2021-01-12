package com.coolbeevip.jpa.persistence.audit;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author zhanglei
 */
@Builder
@ToString
@Getter
public class AuditEvent<T> {
  AuditEventType type;
  T data;
}
