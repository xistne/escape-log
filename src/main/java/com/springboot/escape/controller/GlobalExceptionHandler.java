package com.springboot.escape.controller;

import com.springboot.escape.data.dto.ApiResponse;
import com.springboot.escape.exception.ApiSimpleError;
import com.springboot.escape.exception.AuthException;
import com.springboot.escape.exception.UserException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Optional;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ApiSimpleError> cause = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiSimpleError(error.getField(), Optional.ofNullable(error.getDefaultMessage()).orElse("")))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("VALIDATION_ERROR",
                        "입력값이 올바르지 않습니다.",
                        HttpStatus.BAD_REQUEST,
                        cause,
                        ex.getClass().getSimpleName()));
    }
}
