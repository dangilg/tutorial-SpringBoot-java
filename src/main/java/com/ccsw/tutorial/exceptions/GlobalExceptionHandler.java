package com.ccsw.tutorial.exceptions;

import com.ccsw.tutorial.exceptions.model.ErrorResponseDto;
import org.apache.coyote.Response;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception exception) {
        System.out.println("GlobalExceptionHandler -> Exception.class \n"+exception.toString());
        ErrorResponseDto response = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntime(RuntimeException exception) {
        ErrorResponseDto response = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoIdFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoIdFoundException(NoIdFoundException exception) {
        ErrorResponseDto response = new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundUserException(NotFoundUserException exception) {
        ErrorResponseDto response = new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ErrorResponseDto> handleWrongPasswordException(WrongPasswordException exception) {
        ErrorResponseDto response = new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotValidUsernameException.class)
    public ResponseEntity<ErrorResponseDto> handleNotValidUsernameException(NotValidUsernameException exception) {
        ErrorResponseDto response = new ErrorResponseDto(HttpStatus.CONFLICT.value(), exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotValidTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleNotValidTokenException(NotValidTokenException exception) {
        ErrorResponseDto response = new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
        System.out.println("globalHandlerException -> NotValidToken");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotDeleteableException.class)
    public ResponseEntity<ErrorResponseDto> handleNotDeletableException(NotDeleteableException exception) {
        ErrorResponseDto response = new ErrorResponseDto(HttpStatus.CONFLICT.value(), exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotValidClientNameException.class)
    public ResponseEntity<ErrorResponseDto> handleNotValidClientNameException(NotValidClientNameException exception){
        ErrorResponseDto response = new ErrorResponseDto(HttpStatus.CONFLICT.value(), exception.getMessage());
        return  new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
