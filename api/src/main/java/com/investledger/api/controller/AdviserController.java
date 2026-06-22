package com.investledger.api.controller;

import com.investledger.api.domain.Adviser;
import com.investledger.api.domain.Investor;
import com.investledger.api.repository.AdviserRepository;
import com.investledger.api.repository.InvestorAdviserRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advisers")
public class AdviserController {
    private final AdviserRepository adviserRepository;
    private final InvestorAdviserRepository investorAdviserRepository;

    public AdviserController(AdviserRepository adviserRepository, InvestorAdviserRepository investorAdviserRepository) {
        this.adviserRepository = adviserRepository;
        this.investorAdviserRepository = investorAdviserRepository;
    }

    @GetMapping
    public List<Adviser> getAll() {
        return adviserRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Adviser> getOne(@PathVariable Long id) {
        return adviserRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/investors")
    public List<Investor> getInvestors(@PathVariable Long id) {
        return investorAdviserRepository.findInvestorsByAdviserId(id);
    }
}
