package com.investledger.api.controller;

import com.investledger.api.config.AdviserContext;
import com.investledger.api.domain.Investor;
import com.investledger.api.dto.PortfolioDtos.PortfolioResponse;
import com.investledger.api.repository.InvestorAdviserRepository;
import com.investledger.api.repository.InvestorRepository;
import com.investledger.api.repository.PortfolioRepository;
import com.investledger.api.service.AdviserAccessService;
import com.investledger.api.service.PortfolioService;
import com.investledger.api.service.SummaryDtos.InvestorSummaryDto;
import com.investledger.api.service.SummaryService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/investors")
public class InvestorController {
    private final InvestorRepository investorRepository;
    private final InvestorAdviserRepository investorAdviserRepository;
    private final PortfolioRepository portfolioRepository;
    private final SummaryService summaryService;
    private final PortfolioService portfolioService;
    private final AdviserAccessService adviserAccessService;
    private final AdviserContext adviserContext;

    public InvestorController(
            InvestorRepository investorRepository,
            InvestorAdviserRepository investorAdviserRepository,
            PortfolioRepository portfolioRepository,
            SummaryService summaryService,
            PortfolioService portfolioService,
            AdviserAccessService adviserAccessService,
            AdviserContext adviserContext) {
        this.investorRepository = investorRepository;
        this.investorAdviserRepository = investorAdviserRepository;
        this.portfolioRepository = portfolioRepository;
        this.summaryService = summaryService;
        this.portfolioService = portfolioService;
        this.adviserAccessService = adviserAccessService;
        this.adviserContext = adviserContext;
    }

    @GetMapping
    public List<Investor> getAll() {
        return adviserContext.getAdviserId()
                .map(investorAdviserRepository::findInvestorsByAdviserId)
                .orElseGet(investorRepository::findAll);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Investor> getOne(@PathVariable Long id) {
        adviserAccessService.requireInvestorAccess(id);
        return investorRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/portfolios")
    public List<PortfolioResponse> getPortfolios(@PathVariable Long id) {
        adviserAccessService.requireInvestorAccess(id);
        return portfolioRepository.findByInvestorId(id).stream()
                .map(portfolioService::toResponse)
                .toList();
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<InvestorSummaryDto> getSummary(@PathVariable Long id) {
        adviserAccessService.requireInvestorAccess(id);
        return summaryService.getInvestorSummary(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
