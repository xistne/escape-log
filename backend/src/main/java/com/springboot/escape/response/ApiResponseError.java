package com.springboot.escape.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.springboot.escape.exception.ApiSimpleError;
import com.springboot.escape.exception.CustomException;
import com.springboot.escape.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
@Schema(description = "자세한 오류 내용을 가지는 데이터 객체")
public record ApiResponseError(
        @Schema(description = "커스텀 예외 코드", example = "SIGN_IN_USER_NOT_FOUND")
        String code,
        @Schema(description = "Http Status Code", example = "400")
        Integer status,
        @Schema(description = "커스텀 예외 클래스 이름", example = "UserException")
        String name,
        @Schema(description = "예외 메시지", example = "해당 계정 정보를 찾을 수 없습니다. 다시 시도해주세요.")
        String message,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @Schema(description = "원인 예외 목록 (중첩된 내부 에러)", implementation = ApiSimpleError.class)
        List<ApiSimpleError> cause,
        @Schema(description = "에러 응답 시간", example = "2025-05-20T08:59:00.559874500Z")
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
