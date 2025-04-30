package com.springboot.escape.domain.user.exception;

import com.springboot.escape.exception.CustomException;
import com.springboot.escape.exception.ErrorCode;

public class UserException extends CustomException {
    public UserException() {
        super();
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
