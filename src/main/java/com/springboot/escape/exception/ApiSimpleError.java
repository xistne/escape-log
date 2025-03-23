package com.springboot.escape.exception;

import lombok.Builder;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Builder
public record ApiSimpleError(@NonNull String field, @NonNull String message) {
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