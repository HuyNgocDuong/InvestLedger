package com.investledger.api.repository;

import com.investledger.api.domain.Holding;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    List<Holding> findByPortfolioId(Long portfolioId);
    void deleteByPortfolioId(Long portfolioId);
}
