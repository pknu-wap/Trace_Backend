package com.example.trace.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupRequiredResponse {

    @Schema(description = "회원가입 토큰", example = "511611e7-853...")
    private String signupToken;

    @Schema(description = "user의 providerId", example = "415367..")
    private String providerId;

    @Schema(description = "user의 email", example="sdkfj@naver.com")
    private String email;

    @Schema(description = "user의 nickname", example="nickname")
    private String nickname;

    @Schema(description = "프로필 사진", example="https://example.com/profile.jpg")
    private String profileImage;

    @Schema(description = "회원가입 여부", example = "false")
    boolean isRegistered;
}