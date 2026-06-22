package com.investledger.api.service;

import com.investledger.api.config.AdviserContext;
import com.investledger.api.repository.InvestorAdviserRepository;
import com.investledger.api.repository.PortfolioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdviserAccessService {
    private final AdviserContext adviserContext;
    private final InvestorAdviserRepository investorAdviserRepository;
    private final PortfolioRepository portfolioRepository;

    public AdviserAccessService(
            AdviserContext adviserContext,
            InvestorAdviserRepository investorAdviserRepository,
            PortfolioRepository portfolioRepository) {
        this.adviserContext = adviserContext;
        this.investorAdviserRepository = investorAdviserRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public void requireInvestorAccess(Long investorId) {
        adviserContext.getAdviserId().ifPresent(adviserId -> {
            if (!investorAdviserRepository.adviserHasInvestor(adviserId, investorId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Adviser cannot access this investor");
            }
        });
    }

    public void requirePortfolioAccess(Long portfolioId) {
        adviserContext.getAdviserId().ifPresent(adviserId -> {
            Long investorId = portfolioRepository.findById(portfolioId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Portfolio not found"))
                    .getInvestor()
                    .getId();
            requireInvestorAccess(investorId);
        });
    }
}
