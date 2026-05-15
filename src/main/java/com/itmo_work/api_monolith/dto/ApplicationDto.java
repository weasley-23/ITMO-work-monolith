package com.itmo_work.api_monolith.dto;

import java.time.LocalDateTime;

public record ApplicationDto(
        Long id,
        String coverLetter,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long vacancyId,
        String vacancyTitle,
        Long userId,
        String userFullName
) {
}
