package com.investledger.api.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.investledger.api.domain.Instrument;
import com.investledger.api.domain.Portfolio;
import com.investledger.api.domain.PortfolioTransaction;
import com.investledger.api.repository.HoldingRepository;
import com.investledger.api.repository.InstrumentRepository;
import com.investledger.api.repository.PortfolioTransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class HoldingLedgerServiceTest {

    @Mock private PortfolioTransactionRepository transactionRepository;
    @Mock private HoldingRepository holdingRepository;
    @Mock private InstrumentRepository instrumentRepository;
    @InjectMocks private HoldingLedgerService holdingLedgerService;

    @Test
    void rejectsSellExceedingHoldings() {
        Portfolio portfolio = portfolio(1L);
        Instrument stock = instrument("AAPL");
        PortfolioTransaction buy = txn(portfolio, stock, "BUY", "10", "100", "1000");
        PortfolioTransaction sell = txn(portfolio, stock, "SELL", "15", "110", "1650");

        when(transactionRepository.findByPortfolioIdOrderByTradeDateAscIdAsc(1L)).thenReturn(List.of(buy));

        assertThrows(ResponseStatusException.class, () -> holdingLedgerService.validateTransaction(sell));
    }

    @Test
    void allowsValidBuyAndSellSequence() {
        Portfolio portfolio = portfolio(1L);
        Instrument stock = instrument("AAPL");
        PortfolioTransaction buy = txn(portfolio, stock, "BUY", "10", "100", "1000");
        PortfolioTransaction sell = txn(portfolio, stock, "SELL", "4", "120", "480");

        when(transactionRepository.findByPortfolioIdOrderByTradeDateAscIdAsc(1L)).thenReturn(List.of(buy, sell));

        holdingLedgerService.validateTransaction(sell);
    }

    private Portfolio portfolio(Long id) {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(id);
        return portfolio;
    }

    private Instrument instrument(String symbol) {
        return new Instrument();
    }

    private PortfolioTransaction txn(
            Portfolio portfolio,
            Instrument instrument,
            String type,
            String qty,
            String price,
            String net) {
        PortfolioTransaction transaction = new PortfolioTransaction();
        transaction.setPortfolio(portfolio);
        transaction.setInstrument(instrument);
        transaction.setTransactionType(type);
        transaction.setQuantity(new BigDecimal(qty));
        transaction.setPrice(new BigDecimal(price));
        transaction.setNetAmount(new BigDecimal(net));
        transaction.setTradeDate(LocalDate.now());
        return transaction;
    }
}
