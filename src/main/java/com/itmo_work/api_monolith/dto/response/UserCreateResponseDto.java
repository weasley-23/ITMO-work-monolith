package com.itmo_work.api_monolith.dto.response;

import lombok.Builder;

@Builder
public record UserCreateResponseDto(
        Long id,
        String fullName,
        String email
) {
}
