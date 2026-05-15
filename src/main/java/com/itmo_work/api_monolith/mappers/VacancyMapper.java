package com.itmo_work.api_monolith.mappers;

import com.itmo_work.api_monolith.dto.request.VacancyUpdateRequestDto;
import com.itmo_work.api_monolith.dto.response.VacancyResponseDto;
import com.itmo_work.api_monolith.model.Application;
import com.itmo_work.api_monolith.model.Currency;
import com.itmo_work.api_monolith.model.Vacancy;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VacancyMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "currencyId", target = "currency", qualifiedByName = "mapCurrencyIdToCurrency")
    void update(@MappingTarget Vacancy vacancy, VacancyUpdateRequestDto vacancyUpdateRequestDto);

    @Named("mapCurrencyIdToCurrency")
    default Currency mapCurrencyIdToCurrency(Long currencyId) {
        if (currencyId == null) return null;
        Currency currency = new Currency();
        currency.setId(currencyId);
        return currency;
    }
}