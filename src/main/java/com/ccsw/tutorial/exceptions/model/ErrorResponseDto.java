package com.ccsw.tutorial.exceptions.model;

import java.time.LocalDateTime;

/**
 * @author dgilguti
 * Clase que implementa el DTO de ErrorResponse
 */
public class ErrorResponseDto {

    private int status;
    private String message;
    private LocalDateTime timestamp;

    /**
     * Constructor de ErrorResponseDto
     * @param status int httpStatus
     * @param message String mensaje de la excepcion
     */
    public ErrorResponseDto(int status, String message) {
        setStatus(status);
        setMessage(message);
        timestamp = LocalDateTime.now();
    }

    /**
     * Setter de el Status
     *
     * @param status int httpStatus
     *
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Setter de el Message
     *
     * @param message String mensaje de la excepcion
     *
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter de Status
     * @return int status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Getter de Message
     * @return String message o cadena vacia si message == null
     */
    public String getMessage() {
        if (message == null) {
            return "";
        }
        return message;
    }

    /**
     * Getter de Timestamp
     * @return LocalDateTime timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
