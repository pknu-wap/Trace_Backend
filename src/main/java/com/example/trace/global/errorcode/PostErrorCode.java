package com.example.trace.global.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode{
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    POST_IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 이미지 업로드에 실패했습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    POST_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "게시글 수정 권한이 없습니다."),
    POST_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "게시글 삭제 권한이 없습니다.")
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
