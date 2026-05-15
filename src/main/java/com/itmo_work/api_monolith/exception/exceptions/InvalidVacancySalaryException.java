package com.itmo_work.api_monolith.exception.exceptions;

public class InvalidVacancySalaryException extends RuntimeException {
    public InvalidVacancySalaryException(String message) {
        super(message);
    }
}
