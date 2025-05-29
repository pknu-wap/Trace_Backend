package com.example.trace.global.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode{
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    ALREADY_IN_USE_NICKNAME(HttpStatus.BAD_REQUEST,"닉네임이 중복됩니다.")

    ;
    private final HttpStatus httpStatus;
    private final String message;
}