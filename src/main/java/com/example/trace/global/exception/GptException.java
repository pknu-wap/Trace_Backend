package com.example.trace.global.exception;

import com.example.trace.global.errorcode.AuthErrorCode;
import com.example.trace.global.errorcode.GptErrorCode;
import lombok.Getter;

@Getter
public class GptException extends RuntimeException {
    private final GptErrorCode gptErrorCode;
    private final String failureReason;
    public GptException(GptErrorCode gptErrorCode, String failureReason) {
        super(gptErrorCode.getMessage());
        this.gptErrorCode = gptErrorCode;
        this.failureReason = failureReason;
    }
}