package com.springboot.escape.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    SIGN_UP_FAILED("회원가입에 실패했습니다. 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    private final String message;
    private final HttpStatus status;
    @Override
    public HttpStatus defaultHttpStatus() {
        return status;
    }

    @Override
    public String defaultMessage() {
        return message;
    }

    @Override
    public RuntimeException defaultException() {
        return new UserException(this);
    }

    @Override
    public RuntimeException defaultException(Throwable cause) {
        return new UserException(this, cause);
    }
}
