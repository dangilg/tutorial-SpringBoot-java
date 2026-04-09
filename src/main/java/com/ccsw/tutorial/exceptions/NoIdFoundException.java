package com.ccsw.tutorial.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NoIdFoundException extends RuntimeException {

    public NoIdFoundException() {
        super("OPERATION WITH NO EXISTING ID");
        String message = "OPERATION WITH NO EXISTING ID";

        System.err.println(message);
    }
}
