package com.itmo_work.api_monolith.exception.exceptions;

public class VacancyNotFoundException extends RuntimeException {
    public VacancyNotFoundException() {
        super();
    }

    public VacancyNotFoundException(String message) {
        super(message);
    }
}
