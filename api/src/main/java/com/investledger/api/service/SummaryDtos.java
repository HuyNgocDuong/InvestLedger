package com.investledger.api.service;

import java.math.BigDecimal;
import java.util.List;

public final class SummaryDtos {
    private SummaryDtos() {
    }

    public record AllocationDto(
            String symbol,
            String instrumentName,
            BigDecimal marketValue,
            BigDecimal weightPercent) {
    }

    public record PortfolioSummaryDto(
            Long portfolioId,
            Long investorId,
            BigDecimal totalMarketValue,
            BigDecimal investedMarketValue,
            BigDecimal totalCost,
            BigDecimal unrealizedPnl,
            BigDecimal cashBalance,
            List<AllocationDto> allocations) {
    }

    public record InvestorSummaryDto(
            Long investorId,
            BigDecimal totalMarketValue,
            BigDecimal totalCost,
            BigDecimal unrealizedPnl,
            BigDecimal cashBalance) {
    }
}
