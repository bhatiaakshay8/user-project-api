package com.example.userprojectapi.model.exception;

public class NotAllowedException extends RuntimeException {

    public NotAllowedException() {
        super();
    }

    public NotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAllowedException(String message) {
        super(message);
    }

    public NotAllowedException(Throwable cause) {
        super(cause);
    }
}
