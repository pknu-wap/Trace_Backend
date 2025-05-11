package com.example.trace.global.handler;

import com.example.trace.global.errorcode.AuthErrorCode;
import com.example.trace.global.errorcode.ErrorCode;
import com.example.trace.global.errorcode.SignUpErrorCode;
import com.example.trace.global.errorcode.TokenErrorCode;
import com.example.trace.global.execption.AuthExecption;
import com.example.trace.global.execption.SignUpExecption;
import com.example.trace.global.execption.TokenExecption;
import com.example.trace.global.response.ErrorResponse;
import com.example.trace.global.response.TokenErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExecptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(TokenExecption.class)
    public ResponseEntity<Object> handleTokenException(TokenExecption e) {
        TokenErrorCode tokenErrorCode = e.getTokenErrorCode();
        return handleExceptionInternal(tokenErrorCode);
    }

    @ExceptionHandler(AuthExecption.class)
    public ResponseEntity<Object> handleAuthException(AuthExecption e) {
        AuthErrorCode authErrorCode = e.getAuthErrorCode();
        return handleExceptionInternal(authErrorCode);
    }

    @ExceptionHandler(SignUpExecption.class)
    public ResponseEntity<Object> handleSignUpException(SignUpExecption e) {
        SignUpErrorCode signUpErrorCode = e.getSignUpErrorCode();
        return handleExceptionInternal(signUpErrorCode);
    }



    private ResponseEntity<Object> handleExceptionInternal(TokenErrorCode tokenErrorCode) {
        return ResponseEntity.status(tokenErrorCode.getHttpStatus())
                .body(makeTokenErrorResponse(tokenErrorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(AuthErrorCode authErrorCode) {
        return ResponseEntity.status(authErrorCode.getHttpStatus())
                .body(makeErrorResponse(authErrorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(SignUpErrorCode signUpErrorCode) {
        return ResponseEntity.status(signUpErrorCode.getHttpStatus())
                .body(makeErrorResponse(signUpErrorCode));
    }
    private TokenErrorResponse makeTokenErrorResponse(TokenErrorCode tokenErrorCode) {
        return TokenErrorResponse.builder()
                .code(tokenErrorCode.name())
                .message(tokenErrorCode.getMessage())
                .isExpired(tokenErrorCode.isExpired())
                .isValid(tokenErrorCode.isValid())
                .build();
    }
    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }

}
