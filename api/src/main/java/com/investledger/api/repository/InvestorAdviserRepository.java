package com.investledger.api.repository;

import com.investledger.api.domain.Investor;
import com.investledger.api.domain.InvestorAdviser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvestorAdviserRepository extends JpaRepository<InvestorAdviser, InvestorAdviser.InvestorAdviserId> {
    @Query("""
        SELECT ia.investor FROM InvestorAdviser ia
        WHERE ia.adviser.id = :adviserId
        ORDER BY ia.investor.lastName, ia.investor.firstName
        """)
    List<Investor> findInvestorsByAdviserId(@Param("adviserId") Long adviserId);

    @Query("""
        SELECT COUNT(ia) > 0 FROM InvestorAdviser ia
        WHERE ia.adviser.id = :adviserId AND ia.investor.id = :investorId
        """)
    boolean adviserHasInvestor(@Param("adviserId") Long adviserId, @Param("investorId") Long investorId);
}
