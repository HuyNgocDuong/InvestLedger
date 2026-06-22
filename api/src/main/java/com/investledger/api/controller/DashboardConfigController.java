package com.investledger.api.controller;

import com.investledger.api.config.ApiKeyProperties;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardConfigController {
    private final ApiKeyProperties apiKeyProperties;

    public DashboardConfigController(ApiKeyProperties apiKeyProperties) {
        this.apiKeyProperties = apiKeyProperties;
    }

    @GetMapping("/dashboard/config.json")
    public Map<String, String> dashboardConfig() {
        return Map.of("apiKey", apiKeyProperties.getApiKey());
    }
}
