package com.itmo_work.api_monolith.repository;

import com.itmo_work.api_monolith.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findById(Long currencyId);
}