package com.example.jwttest.dto;

public record JwtDto(
        String accessToken,
        String refreshToken
) {
}
