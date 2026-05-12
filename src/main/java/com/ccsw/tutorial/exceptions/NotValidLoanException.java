package com.ccsw.tutorial.exceptions;

public class NotValidLoanException extends RuntimeException {
    public NotValidLoanException() {
        super("Not Valid Loan");
    }
}
