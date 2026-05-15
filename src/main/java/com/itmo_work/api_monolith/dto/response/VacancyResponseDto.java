package com.itmo_work.api_monolith.dto.response;

import lombok.Builder;

@Builder
public record VacancyResponseDto (
        Long id,
        String title,
        String description,
        Integer salaryFrom,
        Integer salaryTo,
        Long statusId,
        Long companyId,
        Long currencyId
) {
}
