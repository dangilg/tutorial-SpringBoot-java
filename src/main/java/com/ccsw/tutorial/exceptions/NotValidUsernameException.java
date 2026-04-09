package com.ccsw.tutorial.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NotValidUsernameException extends RuntimeException {
    public NotValidUsernameException() {
        super("USER ALREADY EXIST");
        String message = "USER ALREADY EXIST";
        System.err.println(message);
    }
}
