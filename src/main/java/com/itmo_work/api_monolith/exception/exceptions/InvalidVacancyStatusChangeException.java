package com.itmo_work.api_monolith.exception.exceptions;

public class InvalidVacancyStatusChangeException extends RuntimeException {
    public InvalidVacancyStatusChangeException(String message) {
        super(message);
    }
}
