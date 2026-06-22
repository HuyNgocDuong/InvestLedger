package com.investledger.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "portfolio_transaction")
public class PortfolioTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Column(name = "settlement_date")
    private LocalDate settlementDate;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "gross_amount", nullable = false)
    private BigDecimal grossAmount;

    @Column(name = "net_amount", nullable = false)
    private BigDecimal netAmount;

    @Column(nullable = false)
    private String currency;

    @Column
    private String notes;

    public Long getId() { return id; }
    public Portfolio getPortfolio() { return portfolio; }
    public Instrument getInstrument() { return instrument; }
    public LocalDate getTradeDate() { return tradeDate; }
    public LocalDate getSettlementDate() { return settlementDate; }
    public String getTransactionType() { return transactionType; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getGrossAmount() { return grossAmount; }
    public BigDecimal getNetAmount() { return netAmount; }
    public String getCurrency() { return currency; }
    public String getNotes() { return notes; }

    public void setPortfolio(Portfolio portfolio) { this.portfolio = portfolio; }
    public void setInstrument(Instrument instrument) { this.instrument = instrument; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }
    public void setSettlementDate(LocalDate settlementDate) { this.settlementDate = settlementDate; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setGrossAmount(BigDecimal grossAmount) { this.grossAmount = grossAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setNotes(String notes) { this.notes = notes; }
}
