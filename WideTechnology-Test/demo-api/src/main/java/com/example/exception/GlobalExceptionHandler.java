package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Missing required header: " + ex.getHeaderName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid " + ex.getMessage());
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<String> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("An unexpected error occurred: " + e.getMessage());
    }
}