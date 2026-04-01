package com.ccsw.tutorial.exceptions;

public class NotValidUsernameException extends RuntimeException {
    public NotValidUsernameException(String message) {
        super(message);
    }
}
