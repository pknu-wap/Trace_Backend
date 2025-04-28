package com.example.trace.auth.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TokenResponse {
    private final String accessToken;
    private final String refreshToken;
    // Lombok의 @Builder 사용시 모든 필드가 필요하므로 명시적 생성자 추가
}