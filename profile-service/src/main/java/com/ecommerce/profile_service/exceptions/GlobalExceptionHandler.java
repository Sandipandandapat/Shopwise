package com.ecommerce.profile_service.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String,String>> handleUserExistException(UserAlreadyExistsException ex){
        return ResponseEntity
                .badRequest()
                .body(Map.of("error",ex.getMessage()));
    }

}
