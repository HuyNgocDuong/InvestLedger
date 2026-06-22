package com.investledger.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class PortfolioDtos {
    private PortfolioDtos() {
    }

    public record PortfolioRequest(
            @NotNull Long investorId,
            @NotBlank String name,
            @NotBlank @Size(min = 3, max = 3) String currency,
            String status) {
    }

    public record PortfolioUpdateRequest(
            @NotBlank String name,
            @NotBlank @Size(min = 3, max = 3) String currency,
            @NotBlank String status) {
    }

    public record PortfolioResponse(
            Long id,
            Long investorId,
            String name,
            String currency,
            String status) {
    }
}
