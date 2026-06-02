package com.ccsw.tutorial.exceptions;

public class NotValidDtoException extends RuntimeException {
    public NotValidDtoException() {
        super("Dto format is not valid or contains null elements in Not Nullable Fields");
    }
}
