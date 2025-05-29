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
    INVALID_POST_TYPE(HttpStatus.BAD_REQUEST, "잘못된 게시글 타입입니다."),
    POST_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "게시글 수정 권한이 없습니다."),
    POST_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "게시글 삭제 권한이 없습니다."),
    CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "내용이 비어있습니다."),
    TITLE_EMPTY(HttpStatus.BAD_REQUEST, "제목이 비어있습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "댓글 삭제 권한이 없습니다."),
    INVALID_KEYWORD_LENGTH(HttpStatus.BAD_REQUEST,"검색어는 두 글자 이상이어야 합니다."),
    KEYWORD_TOO_LONG(HttpStatus.BAD_REQUEST,"검색어는 50글자 이내여야 합니다."),
    INVALID_KEYWORD(HttpStatus.BAD_REQUEST,"검색어는 특수문자로만 이루어지면 안됩니다.")
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
