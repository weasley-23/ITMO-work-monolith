package com.itmo_work.api_monolith.exception.exceptions;

public class VacancyStatusNotFoundException extends RuntimeException {
    public VacancyStatusNotFoundException() {
        super();
    }

    public VacancyStatusNotFoundException(String message) {
        super(message);
    }
}
