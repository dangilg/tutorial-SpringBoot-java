package com.ccsw.tutorial.exceptions;

public class NotDeleteableException extends RuntimeException {
    public NotDeleteableException(String message) {
        super(message);
    }
}
