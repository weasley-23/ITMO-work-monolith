package com.itmo_work.api_monolith.dto.request;

import com.itmo_work.api_monolith.model.ApplicationStatusName;
import jakarta.validation.constraints.NotNull;

public record ApplicationStatusUpdateRequestDto(
        @NotNull(message = "Status must no be null")
        ApplicationStatusName status
) {
}
