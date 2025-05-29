package com.example.trace.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class UpdateNickNameRequest {

    @Schema(description = "새 닉네임")
    private String nickname;
}
