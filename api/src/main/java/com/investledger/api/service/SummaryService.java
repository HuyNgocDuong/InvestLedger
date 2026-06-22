package com.investledger.api.service;

import com.investledger.api.domain.Holding;
import com.investledger.api.repository.HoldingRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SummaryService {
    private final JdbcTemplate jdbcTemplate;
    private final HoldingRepository holdingRepository;

    public SummaryService(JdbcTemplate jdbcTemplate, HoldingRepository holdingRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.holdingRepository = holdingRepository;
    }

    public Optional<SummaryDtos.PortfolioSummaryDto> getPortfolioSummary(Long portfolioId) {
        String sql = """
            SELECT portfolio_id, investor_id, total_market_value, invested_market_value,
                   total_cost, unrealized_pnl, cash_balance
            FROM portfolio_summary_view
            WHERE portfolio_id = ?
            """;
        Optional<SummaryDtos.PortfolioSummaryDto> summary = jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) return Optional.empty();
            BigDecimal totalMarketValue = rs.getBigDecimal("total_market_value");
            return Optional.of(new SummaryDtos.PortfolioSummaryDto(
                    rs.getLong("portfolio_id"),
                    rs.getLong("investor_id"),
                    totalMarketValue,
                    rs.getBigDecimal("invested_market_value"),
                    rs.getBigDecimal("total_cost"),
                    rs.getBigDecimal("unrealized_pnl"),
                    rs.getBigDecimal("cash_balance"),
                    buildAllocations(portfolioId, totalMarketValue)));
        }, portfolioId);
        return summary;
    }

    public Optional<SummaryDtos.InvestorSummaryDto> getInvestorSummary(Long investorId) {
        String sql = """
            SELECT investor_id, total_market_value, total_cost, unrealized_pnl, cash_balance
            FROM investor_summary_view
            WHERE investor_id = ?
            """;
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) return Optional.empty();
            return Optional.of(new SummaryDtos.InvestorSummaryDto(
                    rs.getLong("investor_id"),
                    rs.getBigDecimal("total_market_value"),
                    rs.getBigDecimal("total_cost"),
                    rs.getBigDecimal("unrealized_pnl"),
                    rs.getBigDecimal("cash_balance")));
        }, investorId);
    }

    private List<SummaryDtos.AllocationDto> buildAllocations(Long portfolioId, BigDecimal totalMarketValue) {
        List<Holding> holdings = holdingRepository.findByPortfolioId(portfolioId);
        List<SummaryDtos.AllocationDto> allocations = new ArrayList<>();
        for (Holding holding : holdings) {
            if ("CASH".equals(holding.getInstrument().getSymbol())) {
                continue;
            }
            BigDecimal weight = BigDecimal.ZERO;
            if (totalMarketValue.compareTo(BigDecimal.ZERO) > 0) {
                weight = holding.getMarketValue()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalMarketValue, 2, RoundingMode.HALF_UP);
            }
            allocations.add(new SummaryDtos.AllocationDto(
                    holding.getInstrument().getSymbol(),
                    holding.getInstrument().getName(),
                    holding.getMarketValue(),
                    weight));
        }
        return allocations;
    }
}
