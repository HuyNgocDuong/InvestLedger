package com.investledger.api.service;

import com.investledger.api.domain.Holding;
import com.investledger.api.domain.Instrument;
import com.investledger.api.domain.Portfolio;
import com.investledger.api.domain.PortfolioTransaction;
import com.investledger.api.repository.HoldingRepository;
import com.investledger.api.repository.InstrumentRepository;
import com.investledger.api.repository.PortfolioTransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HoldingLedgerService {
    private static final String CASH_SYMBOL = "CASH";

    private final PortfolioTransactionRepository transactionRepository;
    private final HoldingRepository holdingRepository;
    private final InstrumentRepository instrumentRepository;

    public HoldingLedgerService(
            PortfolioTransactionRepository transactionRepository,
            HoldingRepository holdingRepository,
            InstrumentRepository instrumentRepository) {
        this.transactionRepository = transactionRepository;
        this.holdingRepository = holdingRepository;
        this.instrumentRepository = instrumentRepository;
    }

    @Transactional
    public void rebuildHoldings(Portfolio portfolio) {
        List<PortfolioTransaction> transactions =
                transactionRepository.findByPortfolioIdOrderByTradeDateAscIdAsc(portfolio.getId());
        Map<Long, PositionState> positions = new HashMap<>();

        for (PortfolioTransaction transaction : transactions) {
            applyTransaction(positions, transaction);
        }

        holdingRepository.deleteByPortfolioId(portfolio.getId());
        holdingRepository.flush();

        for (Map.Entry<Long, PositionState> entry : positions.entrySet()) {
            if (entry.getValue().quantity.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            Instrument instrument = instrumentRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrument not found"));

            Holding holding = new Holding();
            holding.setPortfolio(portfolio);
            holding.setInstrument(instrument);
            holding.setQuantity(entry.getValue().quantity);
            holding.setAverageCost(entry.getValue().averageCost());
            holding.setMarketValue(entry.getValue().quantity.multiply(instrument.getCurrentPrice())
                    .setScale(4, RoundingMode.HALF_UP));
            holdingRepository.save(holding);
        }
    }

    public void validateTransaction(PortfolioTransaction transaction) {
        Map<Long, PositionState> positions = new HashMap<>();
        List<PortfolioTransaction> existing =
                transactionRepository.findByPortfolioIdOrderByTradeDateAscIdAsc(transaction.getPortfolio().getId());

        for (PortfolioTransaction existingTxn : existing) {
            if (transaction.getId() != null && transaction.getId().equals(existingTxn.getId())) {
                continue;
            }
            applyTransaction(positions, existingTxn);
        }
        applyTransaction(positions, transaction);
    }

    private void applyTransaction(Map<Long, PositionState> positions, PortfolioTransaction transaction) {
        Long instrumentId = transaction.getInstrument().getId();
        PositionState state = positions.computeIfAbsent(instrumentId, ignored -> new PositionState());
        String type = transaction.getTransactionType();
        BigDecimal quantity = transaction.getQuantity().abs();
        BigDecimal netAmount = transaction.getNetAmount().abs();

        switch (type) {
            case "BUY" -> {
                BigDecimal totalCost = state.quantity.multiply(state.averageCost).add(netAmount);
                state.quantity = state.quantity.add(quantity);
                if (state.quantity.compareTo(BigDecimal.ZERO) > 0) {
                    state.averageCost = totalCost.divide(state.quantity, 4, RoundingMode.HALF_UP);
                }
            }
            case "SELL" -> {
                ensureSufficient(state, quantity, transaction.getInstrument().getSymbol());
                state.quantity = state.quantity.subtract(quantity);
            }
            case "DEPOSIT" -> state.quantity = state.quantity.add(quantity.max(netAmount));
            case "WITHDRAWAL" -> {
                BigDecimal outflow = quantity.max(netAmount);
                ensureSufficient(state, outflow, transaction.getInstrument().getSymbol());
                state.quantity = state.quantity.subtract(outflow);
            }
            case "DIVIDEND" -> creditCash(positions, netAmount);
            case "FEE" -> debitCash(positions, netAmount);
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unsupported transaction type: " + type);
        }
    }

    private void creditCash(Map<Long, PositionState> positions, BigDecimal amount) {
        Instrument cash = instrumentRepository.findBySymbol(CASH_SYMBOL)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "CASH instrument not configured"));
        PositionState cashState = positions.computeIfAbsent(cash.getId(), ignored -> new PositionState());
        cashState.quantity = cashState.quantity.add(amount);
        cashState.averageCost = BigDecimal.ONE;
    }

    private void debitCash(Map<Long, PositionState> positions, BigDecimal amount) {
        Instrument cash = instrumentRepository.findBySymbol(CASH_SYMBOL)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "CASH instrument not configured"));
        PositionState cashState = positions.computeIfAbsent(cash.getId(), ignored -> new PositionState());
        ensureSufficient(cashState, amount, CASH_SYMBOL);
        cashState.quantity = cashState.quantity.subtract(amount);
        cashState.averageCost = BigDecimal.ONE;
    }

    private void ensureSufficient(PositionState state, BigDecimal amount, String symbol) {
        if (state.quantity.compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Insufficient holdings for " + symbol + ": available " + state.quantity + ", requested " + amount);
        }
    }

    private static final class PositionState {
        private BigDecimal quantity = BigDecimal.ZERO;
        private BigDecimal averageCost = BigDecimal.ZERO;

        private BigDecimal averageCost() {
            return averageCost;
        }
    }
}
