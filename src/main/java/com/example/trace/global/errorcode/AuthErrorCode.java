package com.example.trace.global.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode{
    PUBLIC_KEY_NOT_FOUND( HttpStatus.INTERNAL_SERVER_ERROR, "공개키를 찾을 수 없습니다."),
    INVALID_ID_TOKEN_SIGNATURE( HttpStatus.UNAUTHORIZED, "유효하지 않은 ID 토큰 서명입니다."),
    INVALID_ID_TOKEN_FORMAT( HttpStatus.UNAUTHORIZED, "유효하지 않은 ID 토큰 포맷입니다."),
    INVALID_ID_TOKEN_ISSUER( HttpStatus.UNAUTHORIZED, "유효하지 않은 ID 토큰 발급자입니다."),
    INVALID_ID_TOKEN_AUDIENCE( HttpStatus.UNAUTHORIZED, "유효하지 않은 ID 토큰 수신자입니다."),
    EXPIRED_ID_TOKEN( HttpStatus.UNAUTHORIZED, "ID 토큰이 만료되었습니다."),
    KID_NOT_FOUND( HttpStatus.UNAUTHORIZED, "ID 토큰의 kid를 찾을 수 없습니다."),
    DECODE_ERROR(HttpStatus.UNAUTHORIZED, "잘못된 Base64 인코딩입니다."),
    PARSE_ERROR(HttpStatus.UNAUTHORIZED, "유효하지 않은 json 형식입니다."),
    INTERNAL_SERVER_ERROR( HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
