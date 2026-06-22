package com.investledger.api.service;

import com.investledger.api.domain.Investor;
import com.investledger.api.domain.Portfolio;
import com.investledger.api.dto.PortfolioDtos.PortfolioRequest;
import com.investledger.api.dto.PortfolioDtos.PortfolioResponse;
import com.investledger.api.dto.PortfolioDtos.PortfolioUpdateRequest;
import com.investledger.api.repository.InvestorRepository;
import com.investledger.api.repository.PortfolioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final InvestorRepository investorRepository;
    private final AdviserAccessService adviserAccessService;
    private final AuditLogService auditLogService;

    public PortfolioService(
            PortfolioRepository portfolioRepository,
            InvestorRepository investorRepository,
            AdviserAccessService adviserAccessService,
            AuditLogService auditLogService) {
        this.portfolioRepository = portfolioRepository;
        this.investorRepository = investorRepository;
        this.adviserAccessService = adviserAccessService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public PortfolioResponse create(PortfolioRequest request) {
        adviserAccessService.requireInvestorAccess(request.investorId());
        Investor investor = investorRepository.findById(request.investorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Investor not found"));

        Portfolio portfolio = new Portfolio();
        portfolio.setInvestor(investor);
        portfolio.setName(request.name());
        portfolio.setCurrency(request.currency().toUpperCase());
        portfolio.setStatus(request.status() == null || request.status().isBlank() ? "ACTIVE" : request.status().toUpperCase());

        Portfolio saved = portfolioRepository.save(portfolio);
        auditLogService.log("PORTFOLIO", saved.getId(), "CREATE", "investor=" + investor.getId());
        return toResponse(saved);
    }

    @Transactional
    public PortfolioResponse update(Long id, PortfolioUpdateRequest request) {
        adviserAccessService.requirePortfolioAccess(id);
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Portfolio not found"));

        portfolio.setName(request.name());
        portfolio.setCurrency(request.currency().toUpperCase());
        portfolio.setStatus(request.status().toUpperCase());

        Portfolio saved = portfolioRepository.save(portfolio);
        auditLogService.log("PORTFOLIO", saved.getId(), "UPDATE", "status=" + saved.getStatus());
        return toResponse(saved);
    }

    public PortfolioResponse toResponse(Portfolio portfolio) {
        return new PortfolioResponse(
                portfolio.getId(),
                portfolio.getInvestor().getId(),
                portfolio.getName(),
                portfolio.getCurrency(),
                portfolio.getStatus());
    }
}
