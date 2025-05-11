package com.example.trace.global.execption;

import com.example.trace.global.errorcode.AuthErrorCode;
import com.example.trace.global.errorcode.SignUpErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignUpExecption extends RuntimeException {
    private final SignUpErrorCode signUpErrorCode;
}