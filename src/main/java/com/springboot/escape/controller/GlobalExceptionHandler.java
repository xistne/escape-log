package com.springboot.escape.controller;

import com.springboot.escape.data.dto.ApiResponse;
import com.springboot.escape.exception.AuthException;
import com.springboot.escape.exception.UserException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthException(AuthException e) {
        return ResponseEntity
                .status(e.getErrorCode().defaultHttpStatus())
                .body(ApiResponse.failure(e));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserException(UserException e) {
        return ResponseEntity
                .status(e.getErrorCode().defaultHttpStatus())
                .body(ApiResponse.failure(e));
    }
}
