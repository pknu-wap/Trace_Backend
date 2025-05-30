package com.example.trace.user.dto;

import com.example.trace.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "유저 정보")
public class UserDto {

    @Schema(description = "닉네임")
    String nickname;
    @Schema(description = "프로필 사진")
    String profileImageUrl;
    @Schema(description = "이메일")
    String email;
    @Schema(description = "선행 점수")
    Long verificationScore;

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .email(user.getEmail())
                .verificationScore(user.getVerificationScore())
                .build();
    }
}
