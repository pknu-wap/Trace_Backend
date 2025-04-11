package com.example.trace.dto;

public record JwtDto(
        String accessToken,
        String refreshToken
) {
}
