package com.avnzor.oms_backend.audit.repository;

import com.avnzor.oms_backend.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
