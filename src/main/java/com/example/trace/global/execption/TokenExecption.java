package com.example.trace.global.execption;

import com.example.trace.global.errorcode.TokenErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TokenExecption extends RuntimeException{
    private final TokenErrorCode tokenErrorCode;
}
