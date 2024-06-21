package com.example.userprojectapi.model.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException() {
        super();
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(Throwable cause) {
        super(cause);
    }
}
