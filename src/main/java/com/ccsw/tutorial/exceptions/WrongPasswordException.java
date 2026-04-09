package com.ccsw.tutorial.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException() {
        super("USER OR PASSWORD NOT VALID");
        String message = "USER OR PASSWORD NOT VALID";
        System.err.println(message);
    }
}
