package com.ccsw.tutorial.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NotFoundUserException extends RuntimeException {
    public NotFoundUserException() {
        super("NOT USER FOUND BY THIS USERNAME");
        String message = "NOT USER FOUND BY THIS USERNAME";
        System.err.println(message);
    }
}
