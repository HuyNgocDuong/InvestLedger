package com.investledger.api.repository;

import com.investledger.api.domain.Adviser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdviserRepository extends JpaRepository<Adviser, Long> {
}
