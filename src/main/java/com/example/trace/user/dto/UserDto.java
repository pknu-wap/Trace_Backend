package com.example.trace.user.dto;


import com.example.trace.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Schema(description = "유저 정보")
public class UserDto {

    @Schema(description = "닉네임")
    String nickname;
    @Schema(description = "프로필 사진")
    String profileImageUrl;
    @Schema(description = "이메일")
    String email;

    public UserDto fromEntity(User user) {
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.email = user.getEmail();
        return this;
    }
}
