package com.example.jwttest.exception;

import lombok.Getter;

@Getter
public class SecurityCustomException extends RuntimeException {
    private final TokenErrorCode errorCode;

    public SecurityCustomException(TokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public SecurityCustomException(TokenErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}