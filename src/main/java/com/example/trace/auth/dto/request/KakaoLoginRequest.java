
package com.example.trace.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "카카오 로그인 요청 DTO")
public class KakaoLoginRequest {

    @NotBlank
    @Schema(description = "ID 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwicm9sZSI6IlJPTEVfVVNFUiIsI...")
    private String idToken;
}