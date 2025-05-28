package com.example.trace.global.handler;

import com.example.trace.global.errorcode.*;
import com.example.trace.global.exception.*;
import com.example.trace.global.response.ErrorResponse;
import com.example.trace.global.response.GptErrorResponse;
import com.example.trace.global.response.TokenErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<Object> handleTokenException(TokenException e) {
        TokenErrorCode tokenErrorCode = e.getTokenErrorCode();
        return handleExceptionInternal(tokenErrorCode);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Object> handleAuthException(AuthException e) {
        AuthErrorCode authErrorCode = e.getAuthErrorCode();
        return handleExceptionInternal(authErrorCode);
    }
    @ExceptionHandler(MissionException.class)
    public ResponseEntity<Object> handleMissionException(MissionException e) {
        MissionErrorCode missionErrorCode = e.getMissionErrorCode();
        return handleExceptionInternal(missionErrorCode);
    }


    @ExceptionHandler(SignUpException.class)
    public ResponseEntity<Object> handleSignUpException(SignUpException e) {
        SignUpErrorCode signUpErrorCode = e.getSignUpErrorCode();
        return handleExceptionInternal(signUpErrorCode);
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<Object> handleAuthException(FileException e) {
        FileErrorCode fileErrorCode = e.getFileErrorCode();
        return handleExceptionInternal(fileErrorCode);
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<Object> handleAuthException(PostException e) {
        PostErrorCode postErrorCode = e.getPostErrorCode();
        return handleExceptionInternal(postErrorCode);
    }

    @ExceptionHandler(GptException.class)
    public ResponseEntity<Object> handleGptException(GptException e) {
        GptErrorCode gptErrorCode = e.getGptErrorCode();
        String failureReason = e.getFailureReason();
        return handleExceptionInternal(gptErrorCode,failureReason);
    }

    private ResponseEntity<Object> handleExceptionInternal(TokenErrorCode tokenErrorCode) {
        return ResponseEntity.status(tokenErrorCode.getHttpStatus())
                .body(makeTokenErrorResponse(tokenErrorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(AuthErrorCode authErrorCode) {
        return ResponseEntity.status(authErrorCode.getHttpStatus())
                .body(makeErrorResponse(authErrorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(MissionErrorCode missionErrorCode) {
        return ResponseEntity.status(missionErrorCode.getHttpStatus())
                .body(makeErrorResponse(missionErrorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(SignUpErrorCode signUpErrorCode) {
        return ResponseEntity.status(signUpErrorCode.getHttpStatus())
                .body(makeErrorResponse(signUpErrorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(FileErrorCode fileErrorCode) {
        return ResponseEntity.status(fileErrorCode.getHttpStatus())
                .body(makeErrorResponse(fileErrorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(PostErrorCode postErrorCode) {
        return ResponseEntity.status(postErrorCode.getHttpStatus())
                .body(makeErrorResponse(postErrorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(GptErrorCode gptErrorCode, String failureReason) {
        return ResponseEntity.status(gptErrorCode.getHttpStatus())
                .body(makeGptErrorResponse(gptErrorCode,failureReason));
    }

    private TokenErrorResponse makeTokenErrorResponse(TokenErrorCode tokenErrorCode) {
        return TokenErrorResponse.builder()
                .code(tokenErrorCode.name())
                .message(tokenErrorCode.getMessage())
                .isExpired(tokenErrorCode.isExpired())
                .isValid(tokenErrorCode.isValid())
                .build();
    }

    private GptErrorResponse makeGptErrorResponse(GptErrorCode gptErrorCode, String failureReason) {
        return GptErrorResponse.builder()
                .code(gptErrorCode.name())
                .message(gptErrorCode.getMessage())
                .failureReason(failureReason)
                .build();
    }
    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }

}
