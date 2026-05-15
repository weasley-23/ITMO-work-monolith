package com.itmo_work.api_monolith.mappers;

import com.itmo_work.api_monolith.dto.request.VacancyUpdateRequestDto;
import com.itmo_work.api_monolith.dto.response.VacancyResponseDto;
import com.itmo_work.api_monolith.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class VacancyMapperTest {

    private final VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    private Vacancy vacancy;
    public static final Long COMPANY_ID = 100L;
    public static final Long CURRENCY_ID = 200L;
    public static final Long VACANCY_STATUS_ID = 1L;
    public static final Long VACANCY_ID = 1L;
    private static final Integer SALARY_FROM = 1000;
    private static final Integer SALARY_TO = 2000;
    private static final String VACANCY_TITLE = "Developer";
    private static final String VACANCY_DESCRIPTION = "Backend role";

    @BeforeEach
    void setup() {
        Company company = new Company();
        company.setId(COMPANY_ID);

        Currency currency = new Currency();
        currency.setId(CURRENCY_ID);

        VacancyStatus status = new VacancyStatus(VACANCY_STATUS_ID, VacancyStatusName.PUBLISHED);

        vacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .title(VACANCY_TITLE)
                .description(VACANCY_DESCRIPTION)
                .salaryFrom(SALARY_FROM)
                .salaryTo(SALARY_TO)
                .createdAt(LocalDateTime.now())
                .company(company)
                .status(status)
                .currency(currency)
                .build();
    }

    @Nested
    class MapCurrencyIdToCurrencyTest {

        @Test
        void mapCurrencyIdToCurrencyReturnsCurrencyWithId() {
            Currency result = vacancyMapper.mapCurrencyIdToCurrency(CURRENCY_ID);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(CURRENCY_ID);
        }

        @Test
        void mapCurrencyIdToCurrencyReturnsNullWhenIdNull() {
            assertThat(vacancyMapper.mapCurrencyIdToCurrency(null)).isNull();
        }
    }

    @Nested
    class UpdateTest {

        @Test
        void updateIgnoresNullsAndUpdatesCurrency() {
            final String updatedTitle = "Updated title";
            final Long updatedCurrencyId = 777L;
            VacancyUpdateRequestDto dto =  VacancyUpdateRequestDto.builder()
                            .title(updatedTitle)
                            .currencyId(updatedCurrencyId)
                            .build();


            vacancyMapper.update(vacancy, dto);

            assertThat(vacancy.getTitle()).isEqualTo(updatedTitle);
            assertThat(vacancy.getCurrency()).isNotNull();
            assertThat(vacancy.getCurrency().getId()).isEqualTo(updatedCurrencyId);
        }

        @Test
        void updateDoesNothingWhenAllFieldsNull() {
            VacancyUpdateRequestDto dto =  VacancyUpdateRequestDto.builder()
                    .title(null)
                    .currencyId(null)
                    .build();

            Vacancy before = Vacancy.builder()
                    .id(vacancy.getId())
                    .title(vacancy.getTitle())
                    .description(vacancy.getDescription())
                    .salaryFrom(vacancy.getSalaryFrom())
                    .salaryTo(vacancy.getSalaryTo())
                    .currency(vacancy.getCurrency())
                    .company(vacancy.getCompany())
                    .status(vacancy.getStatus())
                    .build();

            vacancyMapper.update(vacancy, dto);

            assertThat(vacancy.getTitle()).isEqualTo(before.getTitle());
            assertThat(vacancy.getCurrency().getId()).isEqualTo(before.getCurrency().getId());
        }
    }
}
