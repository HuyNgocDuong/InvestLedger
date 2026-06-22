package com.investledger.api;

import com.investledger.api.config.ApiKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApiKeyProperties.class)
public class InvestLedgerApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvestLedgerApplication.class, args);
    }
}
