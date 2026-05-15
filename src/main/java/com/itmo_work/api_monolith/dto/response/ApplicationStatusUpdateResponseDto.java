package com.itmo_work.api_monolith.dto.response;

import java.time.LocalDateTime;

public record ApplicationStatusUpdateResponseDto(
        String status,
        LocalDateTime updatedAt
) {
}
