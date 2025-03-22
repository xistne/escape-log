package com.springboot.escape.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED),
    BLACKLISTED_ACCESS_TOKEN("사용이 중지된 액세스 토큰입니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_ACCESS("접근이 금지되었습니다.", HttpStatus.FORBIDDEN),
    INVALID_ACCESS_TOKEN("유효하지 않은 액세스 토큰입니다.", HttpStatus.UNAUTHORIZED);

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
        return new AuthException(this);
    }

    @Override
    public RuntimeException defaultException(Throwable cause) {
        return new AuthException(this, cause);
    }
}
