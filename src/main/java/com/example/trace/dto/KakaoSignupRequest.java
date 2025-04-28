
package com.example.trace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoSignupRequest {
    @NotBlank
    private String idToken;

    // Additional user information for signup
    private Long ProviderId;
    private String nickname;
    private String email;
    private String profileImage;
    // Add other necessary fields

    private DeviceInfo deviceInfo;
}
