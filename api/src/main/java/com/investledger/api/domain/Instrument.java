package com.investledger.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "instrument")
public class Instrument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol;

    @Column(nullable = false)
    private String name;

    @Column(name = "instrument_type", nullable = false)
    private String instrumentType;

    @Column(nullable = false)
    private String currency;

    @Column(name = "current_price", nullable = false)
    private BigDecimal currentPrice;

    public Long getId() { return id; }
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public String getInstrumentType() { return instrumentType; }
    public String getCurrency() { return currency; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
}
