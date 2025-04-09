
package com.example.trace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoLoginRequest {
    @NotBlank
    private String userId;

    @NotBlank
    private String idToken;

    private DeviceInfo deviceInfo;
}