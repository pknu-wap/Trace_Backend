package com.example.trace.global.exception;

import com.example.trace.global.errorcode.UserErrorCode;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException{
    private final UserErrorCode userErrorCode;

    public UserException(UserErrorCode userErrorCode) {
        super(userErrorCode.getMessage());
        this.userErrorCode = userErrorCode;
    }
}
