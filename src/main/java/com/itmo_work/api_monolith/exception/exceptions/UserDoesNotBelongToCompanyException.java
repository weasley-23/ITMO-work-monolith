package com.itmo_work.api_monolith.exception.exceptions;

public class UserDoesNotBelongToCompanyException extends RuntimeException{
    public UserDoesNotBelongToCompanyException() {
    }

    public UserDoesNotBelongToCompanyException(String message) {
        super(message);
    }
}
