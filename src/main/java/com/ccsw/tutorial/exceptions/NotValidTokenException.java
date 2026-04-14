package com.ccsw.tutorial.exceptions;

public class NotValidTokenException extends RuntimeException {
    public NotValidTokenException() {

        super("TOKEN NOT VALID, MODIFIED OR EXPIRED");
        String errmsg = "TOKEN NOT VALID, MODIFIED OR EXPIRED";
        System.err.println(errmsg);
    }
}
