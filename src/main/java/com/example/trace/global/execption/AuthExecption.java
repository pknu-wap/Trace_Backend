package com.example.trace.global.execption;

import com.example.trace.global.errorcode.AuthErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AuthExecption extends RuntimeException {
    private final AuthErrorCode authErrorCode;
}
