package com.example.trace.auth.dto.request;

import com.example.trace.auth.dto.DeviceInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class KakaoSignupRequest {
    @NotBlank
    private String idToken;
    @NotBlank
    private String nickname;
    private String email;
    private String profileImageUrl; // 프로필 이미지 URL
    
    // 프로필 이미지 파일 업로드를 위한 필드 (직렬화에서 제외)
    @JsonIgnore
    private transient MultipartFile profileImageFile;

    private DeviceInfo deviceInfo;
}
