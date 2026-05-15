package com.itmo_work.api_monolith.service;

import com.itmo_work.api_monolith.exception.exceptions.CurrencyNotFoundException;
import com.itmo_work.api_monolith.model.Currency;
import com.itmo_work.api_monolith.repository.CurrencyRepository;
import com.itmo_work.api_monolith.service.interfaces.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    private CurrencyRepository currencyRepository;

    @Override
    @Transactional
    public Currency findCurrencyById(Long currencyId) {
        return currencyRepository.findById(currencyId)
                .orElseThrow(() -> (new CurrencyNotFoundException("Currency not found")));
    }
}
