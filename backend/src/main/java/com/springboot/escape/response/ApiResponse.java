package com.springboot.escape.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.springboot.escape.exception.ApiSimpleError;
import com.springboot.escape.exception.CustomException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

@Builder
@Schema(description = "공통 api 응답 데이터 객체")
public record ApiResponse<T>(
        @Schema(description = "성공 여부", example = "false")
        boolean success,
        @Schema(description = "Http Status Code", example = "400")
        int status, // DESC : HttpStatus를 쓸 경우 Enum이여서 Json으로 직렬화시 "OK"와 같은 문자열이 됨
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(description = "ResponseDto 객체", example = "SignInResponseDto 객체")
        T data,
        @Schema(description = "실패한 응답일 경우 자세한 오류 내용", implementation = ApiResponseError.class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        ApiResponseError error,
        @Schema(description = "응답시간", example = "2025-05-20T08:59:00.559874500Z")
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
    // TODO : 다른 방법 생각
    public static ApiResponse<Void> failure(String code, String message, HttpStatus status, List<ApiSimpleError> cause, String name) {
        ApiResponseError error = ApiResponseError.builder()
                .code(code)
                .status(status.value())
                .name(name)
                .message(message)
                .cause(cause)
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
