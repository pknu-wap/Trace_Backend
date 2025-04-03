package com.example.jwttest.dto;

import com.example.jwttest.dto.UserResponseDto;

public record AuthResponseDto(
        UserResponseDto.JoinResultDTO user,
        String accessToken,
        String refreshToken
) {}