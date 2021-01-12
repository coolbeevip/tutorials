package com.coolbeevip.jpa;

import com.coolbeevip.jpa.persistence.audit.AuditEntityCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfiguration {
  @Bean
  AuditEntityCallback auditEntityCallback(){
    return new AuditCallbackQueueImpl();
  }
}
