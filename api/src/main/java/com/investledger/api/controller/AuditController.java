package com.investledger.api.controller;

import com.investledger.api.domain.AuditLog;
import com.investledger.api.repository.AuditLogRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
    private final AuditLogRepository auditLogRepository;

    public AuditController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public List<AuditLog> recent() {
        return auditLogRepository.findTop50ByOrderByCreatedAtDesc();
    }
}
