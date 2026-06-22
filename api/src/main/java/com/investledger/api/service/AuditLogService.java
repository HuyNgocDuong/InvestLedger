package com.investledger.api.service;

import com.investledger.api.config.AdviserContext;
import com.investledger.api.domain.AuditLog;
import com.investledger.api.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final AdviserContext adviserContext;

    public AuditLogService(AuditLogRepository auditLogRepository, AdviserContext adviserContext) {
        this.auditLogRepository = auditLogRepository;
        this.adviserContext = adviserContext;
    }

    @Transactional
    public void log(String entityType, Long entityId, String action, String details) {
        AuditLog entry = new AuditLog();
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setAction(action);
        entry.setDetails(details);
        adviserContext.getAdviserId().ifPresent(entry::setAdviserId);
        auditLogRepository.save(entry);
    }
}
