package com.investledger.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.investledger.api.config.AdviserContext;
import com.investledger.api.config.ApiKeyAuthFilter;
import com.investledger.api.domain.Investor;
import com.investledger.api.dto.PortfolioDtos.PortfolioRequest;
import com.investledger.api.dto.PortfolioDtos.PortfolioResponse;
import com.investledger.api.dto.PortfolioDtos.PortfolioUpdateRequest;
import com.investledger.api.dto.TransactionDtos.TransactionRequest;
import com.investledger.api.dto.TransactionDtos.TransactionResponse;
import com.investledger.api.dto.TransactionDtos.TransactionUpdateRequest;
import com.investledger.api.repository.InvestorAdviserRepository;
import com.investledger.api.repository.InvestorRepository;
import com.investledger.api.repository.PortfolioRepository;
import com.investledger.api.repository.PortfolioTransactionRepository;
import com.investledger.api.service.AdviserAccessService;
import com.investledger.api.service.PortfolioService;
import com.investledger.api.service.SummaryService;
import com.investledger.api.service.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {InvestorController.class, PortfolioController.class})
@Import(ApiKeyAuthFilter.class)
@TestPropertySource(properties = "investledger.api-key=test-api-key")
class ApiControllerWebTest {

  private static final String API_KEY = "test-api-key";

  @Autowired private MockMvc mockMvc;

  @MockBean private InvestorRepository investorRepository;
  @MockBean private InvestorAdviserRepository investorAdviserRepository;
  @MockBean private PortfolioRepository portfolioRepository;
  @MockBean private PortfolioTransactionRepository transactionRepository;
  @MockBean private SummaryService summaryService;
  @MockBean private PortfolioService portfolioService;
  @MockBean private TransactionService transactionService;
  @MockBean private AdviserAccessService adviserAccessService;
  @MockBean private AdviserContext adviserContext;

  @Test
  void rejectsMissingApiKey() throws Exception {
    when(adviserContext.getAdviserId()).thenReturn(Optional.empty());
    mockMvc.perform(get("/api/investors")).andExpect(status().isUnauthorized());
  }

  @Test
  void getInvestorsReturnsOkWithApiKey() throws Exception {
    when(adviserContext.getAdviserId()).thenReturn(Optional.empty());
    Investor investor = new Investor();
    when(investorRepository.findAll()).thenReturn(List.of(investor));

    mockMvc.perform(get("/api/investors").header(ApiKeyAuthFilter.API_KEY_HEADER, API_KEY))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void createPortfolioReturnsCreated() throws Exception {
    when(adviserContext.getAdviserId()).thenReturn(Optional.empty());
    PortfolioResponse response =
        new PortfolioResponse(10L, 1L, "Growth", "AUD", "ACTIVE");
    when(portfolioService.create(any(PortfolioRequest.class))).thenReturn(response);

    mockMvc.perform(
            post("/api/portfolios")
                .header(ApiKeyAuthFilter.API_KEY_HEADER, API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"investorId":1,"name":"Growth","currency":"AUD","status":"ACTIVE"}
                    """))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/portfolios/10"))
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.name").value("Growth"));
  }

  @Test
  void updatePortfolioReturnsOk() throws Exception {
    when(adviserContext.getAdviserId()).thenReturn(Optional.empty());
    PortfolioResponse response =
        new PortfolioResponse(10L, 1L, "Updated", "AUD", "CLOSED");
    when(portfolioService.update(any(Long.class), any(PortfolioUpdateRequest.class)))
        .thenReturn(response);

    mockMvc.perform(
            put("/api/portfolios/10")
                .header(ApiKeyAuthFilter.API_KEY_HEADER, API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"name":"Updated","currency":"AUD","status":"CLOSED"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CLOSED"));
  }

  @Test
  void createTransactionReturnsCreated() throws Exception {
    when(adviserContext.getAdviserId()).thenReturn(Optional.empty());
    TransactionResponse response =
        new TransactionResponse(
            99L,
            10L,
            3L,
            LocalDate.of(2025, 6, 1),
            LocalDate.of(2025, 6, 3),
            "BUY",
            new BigDecimal("5"),
            new BigDecimal("100"),
            new BigDecimal("500"),
            new BigDecimal("502"),
            "AUD",
            "Test buy");
    when(transactionService.create(any(Long.class), any(TransactionRequest.class)))
        .thenReturn(response);

    mockMvc.perform(
            post("/api/portfolios/10/transactions")
                .header(ApiKeyAuthFilter.API_KEY_HEADER, API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"instrumentId":3,"tradeDate":"2025-06-01","transactionType":"BUY","quantity":5,"price":100,"currency":"AUD","notes":"Test buy"}
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(99))
        .andExpect(jsonPath("$.transactionType").value("BUY"));
  }

  @Test
  void updateTransactionReturnsOk() throws Exception {
    when(adviserContext.getAdviserId()).thenReturn(Optional.empty());
    TransactionResponse response =
        new TransactionResponse(
            99L,
            10L,
            3L,
            LocalDate.of(2025, 6, 1),
            LocalDate.of(2025, 6, 3),
            "SELL",
            new BigDecimal("2"),
            new BigDecimal("110"),
            new BigDecimal("220"),
            new BigDecimal("218"),
            "AUD",
            "Updated");
    when(transactionService.update(
            any(Long.class), any(Long.class), any(TransactionUpdateRequest.class)))
        .thenReturn(response);

    mockMvc.perform(
            put("/api/portfolios/10/transactions/99")
                .header(ApiKeyAuthFilter.API_KEY_HEADER, API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"instrumentId":3,"tradeDate":"2025-06-01","transactionType":"SELL","quantity":2,"price":110,"currency":"AUD","notes":"Updated"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.transactionType").value("SELL"));
  }
}
