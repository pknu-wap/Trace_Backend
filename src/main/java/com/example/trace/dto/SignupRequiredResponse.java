package com.example.trace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupRequiredResponse {
    private String userId;
    private String email;
    private String nickname;
    private String profileImage;
}