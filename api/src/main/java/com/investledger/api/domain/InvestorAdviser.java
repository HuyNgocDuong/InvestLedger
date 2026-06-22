package com.investledger.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "investor_adviser")
public class InvestorAdviser {
    @EmbeddedId
    private InvestorAdviserId id = new InvestorAdviserId();

    @ManyToOne(optional = false)
    @MapsId("investorId")
    @JoinColumn(name = "investor_id")
    private Investor investor;

    @ManyToOne(optional = false)
    @MapsId("adviserId")
    @JoinColumn(name = "adviser_id")
    private Adviser adviser;

    @Column(nullable = false)
    private String role;

    @Column(name = "since_date", nullable = false)
    private LocalDate sinceDate;

    public Investor getInvestor() { return investor; }
    public Adviser getAdviser() { return adviser; }

    @Embeddable
    public static class InvestorAdviserId implements Serializable {
        private Long investorId;
        private Long adviserId;

        public InvestorAdviserId() {
        }

        public InvestorAdviserId(Long investorId, Long adviserId) {
            this.investorId = investorId;
            this.adviserId = adviserId;
        }

        public Long getInvestorId() { return investorId; }
        public Long getAdviserId() { return adviserId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InvestorAdviserId that)) return false;
            return investorId.equals(that.investorId) && adviserId.equals(that.adviserId);
        }

        @Override
        public int hashCode() {
            return investorId.hashCode() ^ adviserId.hashCode();
        }
    }
}
