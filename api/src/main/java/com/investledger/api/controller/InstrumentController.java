package com.investledger.api.controller;

import com.investledger.api.domain.Instrument;
import com.investledger.api.repository.InstrumentRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instruments")
public class InstrumentController {
    private final InstrumentRepository instrumentRepository;

    public InstrumentController(InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
    }

    @GetMapping
    public List<Instrument> getAll() {
        return instrumentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Instrument> getOne(@PathVariable Long id) {
        return instrumentRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
