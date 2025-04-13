package com.example.trace.admin.dto;

public record JwtDto(
        String accessToken,
        String refreshToken
) {
}
