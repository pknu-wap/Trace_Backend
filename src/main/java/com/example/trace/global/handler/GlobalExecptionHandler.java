package com.example.trace.global.handler;

import com.example.trace.global.errorcode.TokenErrorCode;
import com.example.trace.global.execption.TokenExecption;
import com.example.trace.global.response.TokenErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExecptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(TokenExecption.class)
    public ResponseEntity<Object> handleCustomException(TokenExecption e) {
        TokenErrorCode tokenErrorCode = e.getTokenErrorCode();
        return handleExceptionInternal(tokenErrorCode);
    }
    private ResponseEntity<Object> handleExceptionInternal(TokenErrorCode tokenErrorCode) {
        return ResponseEntity.status(tokenErrorCode.getHttpStatus())
                .body(makeTokenErrorResponse(tokenErrorCode));
    }
    private TokenErrorResponse makeTokenErrorResponse(TokenErrorCode tokenErrorCode) {
        return TokenErrorResponse.builder()
                .code(tokenErrorCode.name())
                .message(tokenErrorCode.getMessage())
                .isExpired(tokenErrorCode.isExpired())
                .isValid(tokenErrorCode.isValid())
                .build();
    }
}
