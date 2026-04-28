package com.ccsw.tutorial.exceptions;

public class NotValidClientNameException extends RuntimeException {
    public NotValidClientNameException() {
        super("CLIENT NAME ALREADY EXIST");
        String message = "CLIENT NAME ALREADY EXIST";
        System.err.println(message);
    }
}
