package com.itmo_work.api_monolith.service.interfaces;

import com.itmo_work.api_monolith.model.Currency;

public interface CurrencyService {
    Currency findCurrencyById(Long currencyId);
}
