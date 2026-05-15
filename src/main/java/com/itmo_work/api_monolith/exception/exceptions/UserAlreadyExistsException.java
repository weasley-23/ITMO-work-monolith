package com.itmo_work.api_monolith.exception.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
