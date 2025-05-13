package com.example.trace.global.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SignUpErrorCode implements ErrorCode {
    NOT_MATCHED_PROVIDER_ID(HttpStatus.BAD_REQUEST, "회원 가입 세션이 없거나, providerId가 일치하지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다."),
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
