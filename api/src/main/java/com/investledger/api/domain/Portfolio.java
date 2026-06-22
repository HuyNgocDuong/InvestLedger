package com.investledger.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "portfolio")
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "investor_id")
    private Investor investor;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String status;

    public Long getId() { return id; }
    public Investor getInvestor() { return investor; }
    public String getName() { return name; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setInvestor(Investor investor) { this.investor = investor; }
    public void setName(String name) { this.name = name; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setStatus(String status) { this.status = status; }
}
