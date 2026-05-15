package com.itmo_work.api_monolith.exception.exceptions;

public class InvalidVacancyStatusException extends RuntimeException {
    public InvalidVacancyStatusException(String message) {
        super(message);
    }
}
