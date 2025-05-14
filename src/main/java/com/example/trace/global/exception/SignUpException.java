package com.example.trace.global.exception;

import com.example.trace.global.errorcode.SignUpErrorCode;
import lombok.Getter;


@Getter
public class SignUpException extends RuntimeException {
    private final SignUpErrorCode signUpErrorCode;

    public SignUpException(SignUpErrorCode signUpErrorCode) {
        super(signUpErrorCode.getMessage());
        this.signUpErrorCode = signUpErrorCode;
    }
}