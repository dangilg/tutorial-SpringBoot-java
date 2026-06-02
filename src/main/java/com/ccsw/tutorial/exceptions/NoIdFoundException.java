package com.ccsw.tutorial.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NoIdFoundException extends RuntimeException {

    public NoIdFoundException() {
        super("OPERATION WITH NOT VALID ID");
        String message = "OPERATION WITH NO VALID ID";

        System.err.println(message);
    }
}
