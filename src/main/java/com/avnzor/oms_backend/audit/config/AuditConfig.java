package com.avnzor.oms_backend.audit.config;

import com.avnzor.oms_backend.audit.filter.AuditLoggingFilter;
import com.avnzor.oms_backend.audit.service.AuditLogService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AuditConfig {

    @Bean
    AuditLoggingFilter auditLoggingFilter(AuditLogService auditLogService) {
        return new AuditLoggingFilter(auditLogService);
    }
}
