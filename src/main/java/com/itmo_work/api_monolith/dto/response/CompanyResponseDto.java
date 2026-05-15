package com.itmo_work.api_monolith.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record CompanyResponseDto (
        Long id,
        String name,
        String email,
        String description,
        String statusMessage,
        Long userId
){
}
