package com.springboot.escape.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Builder
@Schema(description = "중첩된 예외를 분리하여 표현한 에러 정보 객체. 한개의 예외 원인(field+message) 단위로 구성됨.")
public record ApiSimpleError(@NonNull
                             @Schema(description = "발생한 예외 클래스명(에러의 종류)", example = "MethodArgumentNotValidException")
                             String field,
                             @NonNull
                             @Schema(description = "에러에 대한 상세 메시지", example = "이메일은 필수 입력 값입니다.")
                             String message) {
    public static List<ApiSimpleError> listOfCauseSimpleError(Throwable cause) {
        List<ApiSimpleError> result = new ArrayList<>();
        Throwable current = cause;

        while (current != null) {
            String field = current.getClass().getSimpleName();
            String message = current.getLocalizedMessage();

            result.add(ApiSimpleError.builder()
                    .field(field)
                    .message(message)
                    .build());

            current = current.getCause();
        }

        return result;
    }
}