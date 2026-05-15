package com.itmo_work.api_monolith.service;

import com.itmo_work.api_monolith.exception.exceptions.CurrencyNotFoundException;
import com.itmo_work.api_monolith.model.Currency;
import com.itmo_work.api_monolith.repository.CurrencyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    private Currency usd;

    @BeforeEach
    void setup() {
        usd = new Currency(1L, "USD");
    }

    @Nested
    class FindCurrencyByIdTest {

        @Test
        void findCurrencyByIdWhenFound() {
            when(currencyRepository.findById(1L)).thenReturn(Optional.of(usd));

            Currency result = currencyService.findCurrencyById(1L);

            assertThat(result).isEqualTo(usd);

            verify(currencyRepository).findById(1L);
            verifyNoMoreInteractions(currencyRepository);
        }

        @Test
        void findCurrencyByIdWhenNotFound() {
            when(currencyRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> currencyService.findCurrencyById(1L))
                    .isInstanceOf(CurrencyNotFoundException.class)
                    .hasMessage("Currency not found");

            verify(currencyRepository).findById(1L);
            verifyNoMoreInteractions(currencyRepository);
        }
    }
}
