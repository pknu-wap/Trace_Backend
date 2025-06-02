package com.example.trace.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "카카오 회원가입 요청 DTO")
public class KakaoSignupRequest {
    @NotBlank
    @Schema(description = "ID 토큰", example = "511611e7-853...")
    private String signupToken; // 임시 회원가입 토큰

    @NotBlank
    @Schema(description = "회원가입을 위한 providerId", example = "415367..")
    private String providerId;

    @NotBlank
    @Schema(description = "닉네임 설정", example = "닉네임")
    private String nickname;

    @Schema(description = "email 설정", example = "exampl@example.com")
    private String email;

    @Schema(description = "프로필 이미지 url", example = "https://wap-trace-bucket.s3.ap-northeast-2.amazonaws.com/profile/ab54b37b-d46_1.png_4237792414")
    private String profileImageUrl; // 프로필 이미지 URL
    
    // 프로필 이미지 파일 업로드를 위한 필드 (직렬화에서 제외)
    @JsonIgnore
    private transient MultipartFile profileImageFile;


}
