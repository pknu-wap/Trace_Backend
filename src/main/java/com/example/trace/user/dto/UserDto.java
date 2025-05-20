package com.example.trace.user.dto;


import com.example.trace.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    String nickname;
    String profileImageUrl;
    String email;

    public UserDto fromEntity(User user) {
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.email = user.getEmail();
        return this;
    }

    public Long getId() {
        return null;
    }
}
