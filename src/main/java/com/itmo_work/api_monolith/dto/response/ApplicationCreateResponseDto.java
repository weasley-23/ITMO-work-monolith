package com.itmo_work.api_monolith.dto.response;

import java.time.LocalDateTime;

public record ApplicationCreateResponseDto(
        Long id,
        String status,
        LocalDateTime createdAt
) {
}
