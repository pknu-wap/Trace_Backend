package com.example.jwttest.exception;

import lombok.Getter;

@Getter
public class AuthHandler extends RuntimeException {
    private final ErrorStatus errorStatus;

    public AuthHandler(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public AuthHandler(ErrorStatus errorStatus, String customMessage) {
        super(customMessage);
        this.errorStatus = errorStatus;
    }

    public AuthHandler(ErrorStatus errorStatus, Throwable cause) {
        super(errorStatus.getMessage(), cause);
        this.errorStatus = errorStatus;
    }

    public int getHttpStatus() {
        return errorStatus.getHttpStatus().value();
    }
}