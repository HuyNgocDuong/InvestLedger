package com.investledger.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class TransactionDtos {
    private TransactionDtos() {
    }

    public record TransactionRequest(
            @NotNull Long instrumentId,
            @NotNull LocalDate tradeDate,
            LocalDate settlementDate,
            @NotBlank String transactionType,
            @NotNull BigDecimal quantity,
            @NotNull BigDecimal price,
            BigDecimal grossAmount,
            BigDecimal netAmount,
            @NotBlank String currency,
            String notes) {
    }

    public record TransactionUpdateRequest(
            @NotNull Long instrumentId,
            @NotNull LocalDate tradeDate,
            LocalDate settlementDate,
            @NotBlank String transactionType,
            @NotNull BigDecimal quantity,
            @NotNull BigDecimal price,
            BigDecimal grossAmount,
            BigDecimal netAmount,
            @NotBlank String currency,
            String notes) {
    }

    public record TransactionResponse(
            Long id,
            Long portfolioId,
            Long instrumentId,
            LocalDate tradeDate,
            LocalDate settlementDate,
            String transactionType,
            BigDecimal quantity,
            BigDecimal price,
            BigDecimal grossAmount,
            BigDecimal netAmount,
            String currency,
            String notes) {
    }

    public record HoldingResponse(
            Long id,
            Long instrumentId,
            String instrumentSymbol,
            String instrumentName,
            BigDecimal quantity,
            BigDecimal averageCost,
            BigDecimal marketValue) {
    }
}
