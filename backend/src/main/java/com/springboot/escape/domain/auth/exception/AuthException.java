package com.springboot.escape.domain.auth.exception;

import com.springboot.escape.exception.CustomException;
import com.springboot.escape.exception.ErrorCode;

public class AuthException extends CustomException {

    public AuthException() {
        super();
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
