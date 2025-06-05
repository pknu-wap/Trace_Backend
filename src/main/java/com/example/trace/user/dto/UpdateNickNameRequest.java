package com.example.trace.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateNickNameRequest {

    @NotBlank(message = "닉네임은 공백만으로 이루어질 수 없습니다")
    @Size(min = 2, max = 12, message = "닉네임은 2-12자 이내여야 합니다")
    @Schema(description = "새 닉네임", example = "홍길동")
    private String nickname;
}
