package com.itmo_work.api_monolith.exception.exceptions;

public class ApplicationStatusNotFoundException extends RuntimeException {
    public ApplicationStatusNotFoundException() {
    }

    public ApplicationStatusNotFoundException(String message) {
        super(message);
    }
}
