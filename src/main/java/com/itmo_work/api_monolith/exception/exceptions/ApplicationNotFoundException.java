package com.itmo_work.api_monolith.exception.exceptions;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException() {
    }

    public ApplicationNotFoundException(String message) {
        super(message);
    }
}
