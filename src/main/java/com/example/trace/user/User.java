package com.example.trace.user;

import com.example.trace.gpt.dto.VerificationDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String providerId; //provider에서 받아온 userId

    @Column(nullable = false)
    private String provider;

    private String email;

    private String nickname;

    private String profileImageUrl;

    @Builder.Default
    private Long verificationScore = 0L;

    @Builder.Default
    private Long verificationCount = 0L;

    //spring security용으로 일단 두기.
    private String password;
    private String username;
    private String role;

    public List<String> getRoleList() {
        if(this.role != null && !this.role.isEmpty()) {
            return Arrays.asList(this.role.split(","));
        }
        return new ArrayList<>();
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void updateProfileImageUrl(String newProfileImageUrl) {
        this.profileImageUrl = newProfileImageUrl;
    }

    public void updateVerification(VerificationDto verificationDto){
        if(verificationDto.isTextResult() || verificationDto.isImageResult()) verificationCount++;
        if(verificationDto.isImageResult()) this.verificationScore += 10;
        if(verificationDto.isTextResult()) this.verificationScore += 5;
    }
}