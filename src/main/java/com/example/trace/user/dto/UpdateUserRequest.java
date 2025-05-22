package com.example.trace.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    @Schema(description = "새 닉네임")
    private String nickname;

    @Schema(description = "새 프로필 이미지 URL")
    private String profileImageUrl;
}
