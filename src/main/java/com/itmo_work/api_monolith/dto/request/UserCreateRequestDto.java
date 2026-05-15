package com.itmo_work.api_monolith.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserCreateRequestDto(

        @NotBlank(message = "Full name must not be blank")
        @Size(min = 2, max = 255, message = "Full name must be between 2 and 255 characters")
        String fullName,

        @NotBlank(message = "Email must not be blank")
        @Size(min = 5, max = 320, message = "User email must be between 5 and 320 characters")
        String email,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 5, max = 255, message = "Password must be between 5 and 255 characters")
        String password
) {

}
