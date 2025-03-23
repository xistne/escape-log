package com.springboot.escape.data.dto;

import com.springboot.escape.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        int status, // DESC : HttpStatus를 쓸 경우 Enum이여서 Json으로 직렬화시 "OK"와 같은 문자열이 됨
         T data,
        ApiResponseError error,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(T data, HttpStatus status) {
        return new ApiResponse<>(
                true,
                status.value(),
                data,
                null,
                Instant.now()
        );
    }
    public static ApiResponse<Void> failure(CustomException exception) {
        ApiResponseError error = ApiResponseError.of(exception);
        return new ApiResponse<>(
                false,
                error.status(),
                null,
                error,
                error.timestamp()
        );
    }

    public static ApiResponse<Void> failure(String code, String message, HttpStatus status) {
        ApiResponseError error = ApiResponseError.builder()
                .code(code)
                .status(status.value())
                .message(message)
                .timestamp(Instant.now())
                .build();

        return new ApiResponse<>(
                false,
                status.value(),
                null,
                error,
                Instant.now()
        );
    }
}
