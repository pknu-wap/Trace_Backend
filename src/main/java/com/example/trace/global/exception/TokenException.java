package com.example.trace.global.exception;

import com.example.trace.global.errorcode.TokenErrorCode;
import lombok.Getter;


@Getter
public class TokenException extends RuntimeException{
    private final TokenErrorCode tokenErrorCode;

    public TokenException(TokenErrorCode tokenErrorCode) {
        super(tokenErrorCode.getMessage());
        this.tokenErrorCode = tokenErrorCode;
    }
}
