package com.example.jwttest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinResultDTO {
        private Long id;
        private String username;
        private String name;
        private String email;
        private String role;
        private Long kakaoId;
    }
}