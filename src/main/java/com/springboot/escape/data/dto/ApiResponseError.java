package com.springboot.escape.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.springboot.escape.exception.ApiSimpleError;
import com.springboot.escape.exception.CustomException;
import com.springboot.escape.exception.ErrorCode;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ApiResponseError(
        String code,
        Integer status,
        String name,
        String message,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) List<ApiSimpleError> cause,
        Instant timestamp
) {
    public static ApiResponseError of(CustomException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        String errorName = exception.getClass().getName();
        errorName = errorName.substring(errorName.lastIndexOf('.') + 1);

        return ApiResponseError.builder()
                .code(errorCode.name())
                .status(errorCode.defaultHttpStatus().value())
                .name(errorName)
                .message(exception.getMessage())
                .cause(ApiSimpleError.listOfCauseSimpleError(exception.getCause()))
                .build();
    }

    public ApiResponseError {
        if (code == null) {
            code = "API_ERROR";
        }

        if (status == null) {
            status = 500;
        }

        if (name == null) {
            name = "ApiError";
        }

        if (message == null || message.isBlank()) {
            message = "API 오류";
        }

        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
