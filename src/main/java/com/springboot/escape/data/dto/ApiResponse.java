package com.springboot.escape.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.springboot.escape.exception.CustomException;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Builder
public record ApiResponse<T>(
        boolean success,
        int status, // DESC : HttpStatus를 쓸 경우 Enum이여서 Json으로 직렬화시 "OK"와 같은 문자열이 됨
        @JsonInclude(JsonInclude.Include.NON_NULL)
        T data,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        ApiResponseError error,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(T data, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status.value())
                .data(data)
                .timestamp(Instant.now())
                .build();
    }
    public static ApiResponse<Void> success(HttpStatus status) {
        return ApiResponse.<Void>builder()
                .success(true)
                .status(status.value())
                .timestamp(Instant.now())
                .build();
    }
    public static ApiResponse<Void> failure(CustomException exception) {
        ApiResponseError error = ApiResponseError.of(exception);
        return ApiResponse.<Void>builder()
                .success(false)
                .status(error.status())
                .error(error)
                .timestamp(error.timestamp())
                .build();
    }

    public static ApiResponse<Void> failure(String code, String message, HttpStatus status) {
        ApiResponseError error = ApiResponseError.builder()
                .code(code)
                .status(status.value())
                .message(message)
                .timestamp(Instant.now())
                .build();
        return ApiResponse.<Void>builder()
                .success(false)
                .status(status.value())
                .error(error)
                .timestamp(Instant.now())
                .build();
    }
}
