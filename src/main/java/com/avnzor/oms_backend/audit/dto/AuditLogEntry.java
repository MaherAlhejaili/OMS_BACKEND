package com.avnzor.oms_backend.audit.dto;

import java.util.Map;

public record AuditLogEntry(
        String eventType,
        String entityType,
        String entityId,
        String actor,
        String tenantId,
        Map<String, Object> details
) {
}
