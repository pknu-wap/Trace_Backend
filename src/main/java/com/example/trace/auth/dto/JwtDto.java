package com.example.trace.auth.dto;

public record JwtDto(
        String accessToken,
        String refreshToken
) {
}
