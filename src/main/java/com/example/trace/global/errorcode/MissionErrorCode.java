package com.example.trace.global.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MissionErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"사용자가 존재하지 않습니다."),
    DAILYMISSION_NOT_FOUND(HttpStatus.NOT_FOUND,"사용자에게 할당된 일일 미션이 없습니다"),
    MISSION_CREATION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST,"일일 변경 횟수 초과"),
    RANDOM_MISSION_NOT_FOUND(HttpStatus.NOT_FOUND,"다른 미션을 찾을 수 없습니다.")
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
