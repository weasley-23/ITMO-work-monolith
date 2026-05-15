package com.itmo_work.api_monolith.exception.exceptions;

public class VacancyNotOpenedException extends RuntimeException {
    public VacancyNotOpenedException() {
    }

    public VacancyNotOpenedException(String message) {
        super(message);
    }
}
