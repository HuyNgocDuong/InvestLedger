package com.investledger.api.service;

import com.investledger.api.domain.Holding;
import com.investledger.api.domain.Instrument;
import com.investledger.api.domain.Portfolio;
import com.investledger.api.domain.PortfolioTransaction;
import com.investledger.api.dto.TransactionDtos.HoldingResponse;
import com.investledger.api.dto.TransactionDtos.TransactionRequest;
import com.investledger.api.dto.TransactionDtos.TransactionResponse;
import com.investledger.api.dto.TransactionDtos.TransactionUpdateRequest;
import com.investledger.api.repository.HoldingRepository;
import com.investledger.api.repository.InstrumentRepository;
import com.investledger.api.repository.PortfolioRepository;
import com.investledger.api.repository.PortfolioTransactionRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransactionService {
    private final PortfolioRepository portfolioRepository;
    private final InstrumentRepository instrumentRepository;
    private final PortfolioTransactionRepository transactionRepository;
    private final HoldingRepository holdingRepository;
    private final HoldingLedgerService holdingLedgerService;
    private final AdviserAccessService adviserAccessService;
    private final AuditLogService auditLogService;

    public TransactionService(
            PortfolioRepository portfolioRepository,
            InstrumentRepository instrumentRepository,
            PortfolioTransactionRepository transactionRepository,
            HoldingRepository holdingRepository,
            HoldingLedgerService holdingLedgerService,
            AdviserAccessService adviserAccessService,
            AuditLogService auditLogService) {
        this.portfolioRepository = portfolioRepository;
        this.instrumentRepository = instrumentRepository;
        this.transactionRepository = transactionRepository;
        this.holdingRepository = holdingRepository;
        this.holdingLedgerService = holdingLedgerService;
        this.adviserAccessService = adviserAccessService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public TransactionResponse create(Long portfolioId, TransactionRequest request) {
        Portfolio portfolio = findPortfolio(portfolioId);
        adviserAccessService.requirePortfolioAccess(portfolioId);
        Instrument instrument = findInstrument(request.instrumentId());
        PortfolioTransaction transaction = applyRequest(new PortfolioTransaction(), portfolio, instrument, request);
        holdingLedgerService.validateTransaction(transaction);
        PortfolioTransaction saved = transactionRepository.save(transaction);
        holdingLedgerService.rebuildHoldings(portfolio);
        auditLogService.log("TRANSACTION", saved.getId(), "CREATE",
                "portfolio=" + portfolioId + ", type=" + saved.getTransactionType());
        return toResponse(saved);
    }

    @Transactional
    public TransactionResponse update(Long portfolioId, Long transactionId, TransactionUpdateRequest request) {
        Portfolio portfolio = findPortfolio(portfolioId);
        adviserAccessService.requirePortfolioAccess(portfolioId);
        PortfolioTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        if (!transaction.getPortfolio().getId().equals(portfolioId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found in portfolio");
        }

        Instrument instrument = findInstrument(request.instrumentId());
        applyUpdate(transaction, portfolio, instrument, request);
        holdingLedgerService.validateTransaction(transaction);
        PortfolioTransaction saved = transactionRepository.save(transaction);
        holdingLedgerService.rebuildHoldings(portfolio);
        auditLogService.log("TRANSACTION", saved.getId(), "UPDATE",
                "portfolio=" + portfolioId + ", type=" + saved.getTransactionType());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long portfolioId, Long transactionId) {
        Portfolio portfolio = findPortfolio(portfolioId);
        adviserAccessService.requirePortfolioAccess(portfolioId);
        PortfolioTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
        if (!transaction.getPortfolio().getId().equals(portfolioId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found in portfolio");
        }
        transactionRepository.delete(transaction);
        holdingLedgerService.rebuildHoldings(portfolio);
        auditLogService.log("TRANSACTION", transactionId, "DELETE", "portfolio=" + portfolioId);
    }

    @Transactional
    public void rebuildHoldings(Long portfolioId) {
        Portfolio portfolio = findPortfolio(portfolioId);
        adviserAccessService.requirePortfolioAccess(portfolioId);
        holdingLedgerService.rebuildHoldings(portfolio);
        auditLogService.log("PORTFOLIO", portfolioId, "REBUILD_HOLDINGS", "manual rebuild");
    }

    public List<HoldingResponse> getHoldings(Long portfolioId) {
        adviserAccessService.requirePortfolioAccess(portfolioId);
        findPortfolio(portfolioId);
        return holdingRepository.findByPortfolioId(portfolioId).stream()
                .map(this::toHoldingResponse)
                .toList();
    }

    private Portfolio findPortfolio(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Portfolio not found"));
    }

    private Instrument findInstrument(Long instrumentId) {
        return instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrument not found"));
    }

    private PortfolioTransaction applyRequest(
            PortfolioTransaction transaction,
            Portfolio portfolio,
            Instrument instrument,
            TransactionRequest request) {
        transaction.setPortfolio(portfolio);
        transaction.setInstrument(instrument);
        transaction.setTradeDate(request.tradeDate());
        transaction.setSettlementDate(request.settlementDate() != null ? request.settlementDate() : request.tradeDate());
        transaction.setTransactionType(request.transactionType().toUpperCase());
        transaction.setQuantity(request.quantity().abs());
        transaction.setPrice(request.price());
        transaction.setGrossAmount(resolveGross(request.grossAmount(), request.quantity(), request.price()));
        transaction.setNetAmount(resolveNet(request.netAmount(), transaction.getGrossAmount()));
        transaction.setCurrency(request.currency().toUpperCase());
        transaction.setNotes(request.notes());
        return transaction;
    }

    private void applyUpdate(
            PortfolioTransaction transaction,
            Portfolio portfolio,
            Instrument instrument,
            TransactionUpdateRequest request) {
        transaction.setPortfolio(portfolio);
        transaction.setInstrument(instrument);
        transaction.setTradeDate(request.tradeDate());
        transaction.setSettlementDate(request.settlementDate() != null ? request.settlementDate() : request.tradeDate());
        transaction.setTransactionType(request.transactionType().toUpperCase());
        transaction.setQuantity(request.quantity().abs());
        transaction.setPrice(request.price());
        transaction.setGrossAmount(resolveGross(request.grossAmount(), request.quantity(), request.price()));
        transaction.setNetAmount(resolveNet(request.netAmount(), transaction.getGrossAmount()));
        transaction.setCurrency(request.currency().toUpperCase());
        transaction.setNotes(request.notes());
    }

    private BigDecimal resolveGross(BigDecimal gross, BigDecimal quantity, BigDecimal price) {
        if (gross != null) {
            return gross;
        }
        return quantity.abs().multiply(price);
    }

    private BigDecimal resolveNet(BigDecimal net, BigDecimal gross) {
        if (net != null) {
            return net;
        }
        return gross;
    }

    public TransactionResponse toResponse(PortfolioTransaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getPortfolio().getId(),
                transaction.getInstrument().getId(),
                transaction.getTradeDate(),
                transaction.getSettlementDate(),
                transaction.getTransactionType(),
                transaction.getQuantity(),
                transaction.getPrice(),
                transaction.getGrossAmount(),
                transaction.getNetAmount(),
                transaction.getCurrency(),
                transaction.getNotes());
    }

    private HoldingResponse toHoldingResponse(Holding holding) {
        return new HoldingResponse(
                holding.getId(),
                holding.getInstrument().getId(),
                holding.getInstrument().getSymbol(),
                holding.getInstrument().getName(),
                holding.getQuantity(),
                holding.getAverageCost(),
                holding.getMarketValue());
    }
}
