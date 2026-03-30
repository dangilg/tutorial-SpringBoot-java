package com.ccsw.tutorial.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoIdFoundException extends RuntimeException {

    public NoIdFoundException(String message) {
        super(message);
    }
}
