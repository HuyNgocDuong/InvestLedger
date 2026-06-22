package com.investledger.api.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "name", "InvestLedger API",
                "status", "running",
                "dashboard", "/dashboard/index.html",
                "docs", "Use /api/* with headers X-API-Key and optional X-Adviser-Id");
    }

    @GetMapping("/api")
    public Map<String, Object> apiIndex() {
        return Map.of(
                "endpoints", Map.of(
                        "investors", "/api/investors",
                        "portfolios", "/api/portfolios",
                        "advisers", "/api/advisers",
                        "instruments", "/api/instruments"),
                "auth", "Send header X-API-Key on all /api/** requests");
    }
}
