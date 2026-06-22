package com.investledger.api.repository;

import com.investledger.api.domain.PortfolioTransaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioTransactionRepository extends JpaRepository<PortfolioTransaction, Long> {
    List<PortfolioTransaction> findByPortfolioIdOrderByTradeDateDesc(Long portfolioId);
    List<PortfolioTransaction> findByPortfolioIdOrderByTradeDateAscIdAsc(Long portfolioId);
}
