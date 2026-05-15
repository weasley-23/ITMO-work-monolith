package com.itmo_work.api_monolith.exception.exceptions;

public class UserHasAlreadyAppliedException extends RuntimeException {
    public UserHasAlreadyAppliedException() {
    }

    public UserHasAlreadyAppliedException(String message) {
        super(message);
    }
}
