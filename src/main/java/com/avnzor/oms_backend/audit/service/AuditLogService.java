package com.avnzor.oms_backend.audit.service;

import com.avnzor.oms_backend.audit.dto.AuditLogEntry;
import com.avnzor.oms_backend.audit.entity.AuditLog;
import com.avnzor.oms_backend.audit.repository.AuditLogRepository;
import com.avnzor.oms_backend.tenants.context.TenantContext;
import com.avnzor.oms_backend.tenants.context.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    public static final String API_REQUEST_EVENT = "API_REQUEST";

    private final AuditLogRepository auditLogRepository;

    @Async
    @Transactional
    public void logAsync(AuditLogEntry entry, TenantContext tenantContext) {
        if (tenantContext == null) {
            return;
        }

        try {
            TenantContextHolder.set(tenantContext);
            persist(entry);
        } catch (Exception exception) {
            log.error("Failed to persist audit log for eventType={}", entry.eventType(), exception);
        } finally {
            TenantContextHolder.clear();
        }
    }

    @Transactional
    public AuditLog log(AuditLogEntry entry) {
        return persist(entry);
    }

    private AuditLog persist(AuditLogEntry entry) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEventType(entry.eventType());
        auditLog.setEntityType(entry.entityType());
        auditLog.setEntityId(entry.entityId());
        auditLog.setActor(entry.actor());
        auditLog.setDetails(entry.details());
        return auditLogRepository.save(auditLog);
    }
}
