package com.investledger.api.controller;

import com.investledger.api.dto.PortfolioDtos.PortfolioRequest;
import com.investledger.api.dto.PortfolioDtos.PortfolioResponse;
import com.investledger.api.dto.PortfolioDtos.PortfolioUpdateRequest;
import com.investledger.api.dto.TransactionDtos.HoldingResponse;
import com.investledger.api.dto.TransactionDtos.TransactionRequest;
import com.investledger.api.dto.TransactionDtos.TransactionResponse;
import com.investledger.api.dto.TransactionDtos.TransactionUpdateRequest;
import com.investledger.api.repository.PortfolioRepository;
import com.investledger.api.repository.PortfolioTransactionRepository;
import com.investledger.api.service.AdviserAccessService;
import com.investledger.api.service.PortfolioService;
import com.investledger.api.service.SummaryDtos.PortfolioSummaryDto;
import com.investledger.api.service.SummaryService;
import com.investledger.api.service.TransactionService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioTransactionRepository transactionRepository;
    private final SummaryService summaryService;
    private final PortfolioService portfolioService;
    private final TransactionService transactionService;
    private final AdviserAccessService adviserAccessService;

    public PortfolioController(
            PortfolioRepository portfolioRepository,
            PortfolioTransactionRepository transactionRepository,
            SummaryService summaryService,
            PortfolioService portfolioService,
            TransactionService transactionService,
            AdviserAccessService adviserAccessService) {
        this.portfolioRepository = portfolioRepository;
        this.transactionRepository = transactionRepository;
        this.summaryService = summaryService;
        this.portfolioService = portfolioService;
        this.transactionService = transactionService;
        this.adviserAccessService = adviserAccessService;
    }

    @GetMapping
    public List<PortfolioResponse> getAll() {
        return portfolioRepository.findAll().stream()
                .filter(portfolio -> {
                    try {
                        adviserAccessService.requirePortfolioAccess(portfolio.getId());
                        return true;
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .map(portfolioService::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponse> getOne(@PathVariable Long id) {
        adviserAccessService.requirePortfolioAccess(id);
        return portfolioRepository.findById(id)
                .map(portfolioService::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PortfolioResponse> create(@Valid @RequestBody PortfolioRequest request) {
        PortfolioResponse created = portfolioService.create(request);
        return ResponseEntity.created(URI.create("/api/portfolios/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortfolioResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PortfolioUpdateRequest request) {
        return ResponseEntity.ok(portfolioService.update(id, request));
    }

    @GetMapping("/{id}/holdings")
    public List<HoldingResponse> getHoldings(@PathVariable Long id) {
        return transactionService.getHoldings(id);
    }

    @GetMapping("/{id}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable Long id) {
        return transactionRepository.findByPortfolioIdOrderByTradeDateDesc(id).stream()
                .map(transactionService::toResponse)
                .toList();
    }

    @PostMapping("/{id}/transactions")
    public ResponseEntity<TransactionResponse> createTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse created = transactionService.create(id, request);
        return ResponseEntity.created(URI.create("/api/portfolios/" + id + "/transactions/" + created.id())).body(created);
    }

    @PutMapping("/{id}/transactions/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionUpdateRequest request) {
        return ResponseEntity.ok(transactionService.update(id, transactionId, request));
    }

    @DeleteMapping("/{id}/transactions/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id, @PathVariable Long transactionId) {
        transactionService.delete(id, transactionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/holdings/rebuild")
    public ResponseEntity<Void> rebuildHoldings(@PathVariable Long id) {
        transactionService.rebuildHoldings(id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<PortfolioSummaryDto> getSummary(@PathVariable Long id) {
        adviserAccessService.requirePortfolioAccess(id);
        return summaryService.getPortfolioSummary(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
