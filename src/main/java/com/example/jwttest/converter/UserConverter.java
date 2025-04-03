package com.example.jwttest.converter;

import com.example.jwttest.domain.User;
import com.example.jwttest.dto.UserResponseDto;

public class UserConverter {

    public static UserResponseDto.JoinResultDTO toJoinResultDTO(User user) {
        return UserResponseDto.JoinResultDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .kakaoId(user.getKakaoid() != null ? Long.valueOf(user.getKakaoid()) : null)
                .build();
    }
}