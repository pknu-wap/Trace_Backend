package com.example.trace.global.exception;

import com.example.trace.global.errorcode.PostErrorCode;
import lombok.Getter;


@Getter
public class PostException extends RuntimeException {
    private final PostErrorCode postErrorCode;
    public PostException(PostErrorCode postErrorCode) {
        super(postErrorCode.getMessage());
        this.postErrorCode = postErrorCode;
    }
}

