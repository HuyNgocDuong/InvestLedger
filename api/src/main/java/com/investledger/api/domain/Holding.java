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

@Entity
@Table(name = "holding")
public class Holding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "average_cost", nullable = false)
    private BigDecimal averageCost;

    @Column(name = "market_value", nullable = false)
    private BigDecimal marketValue;

    public Long getId() { return id; }
    public Portfolio getPortfolio() { return portfolio; }
    public Instrument getInstrument() { return instrument; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getAverageCost() { return averageCost; }
    public BigDecimal getMarketValue() { return marketValue; }

    public void setPortfolio(Portfolio portfolio) { this.portfolio = portfolio; }
    public void setInstrument(Instrument instrument) { this.instrument = instrument; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public void setAverageCost(BigDecimal averageCost) { this.averageCost = averageCost; }
    public void setMarketValue(BigDecimal marketValue) { this.marketValue = marketValue; }
}
