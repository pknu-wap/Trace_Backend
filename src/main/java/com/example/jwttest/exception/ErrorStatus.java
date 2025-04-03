package com.example.jwttest.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    // 공통 오류
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),

    // 파싱 관련 오류
    _PARSING_ERROR(HttpStatus.BAD_REQUEST, "데이터 파싱 중 오류가 발생했습니다."),

    // 인증 관련 오류
    _INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    _EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),

    // OAuth 관련 오류
    _OAUTH_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OAuth 서버와 통신 중 오류가 발생했습니다."),
    _OAUTH_USER_INFO_ERROR(HttpStatus.BAD_REQUEST, "OAuth 사용자 정보를 가져오는 데 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
