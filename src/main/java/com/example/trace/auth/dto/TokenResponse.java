package com.example.trace.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "토큰 응답 DTO")
public class TokenResponse {
    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwicm9sZSI6IlJPTEVfVVNFUiIsI...")
    private final String accessToken;
    
    @Schema(description = "JWT 리프레시 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwicm9sZSI6IlJPTEVfVVNFUiIsI...")
    private final String refreshToken;
}