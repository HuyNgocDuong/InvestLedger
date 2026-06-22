package com.investledger.api.service;

import com.investledger.api.domain.Portfolio;
import com.investledger.api.repository.PortfolioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class HoldingsRebuildRunner implements ApplicationRunner {
    private final PortfolioRepository portfolioRepository;
    private final HoldingLedgerService holdingLedgerService;

    public HoldingsRebuildRunner(
            PortfolioRepository portfolioRepository,
            HoldingLedgerService holdingLedgerService) {
        this.portfolioRepository = portfolioRepository;
        this.holdingLedgerService = holdingLedgerService;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (Portfolio portfolio : portfolioRepository.findAll()) {
            holdingLedgerService.rebuildHoldings(portfolio);
        }
    }
}
