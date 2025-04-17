
package com.example.trace.auth.dto.request;

import com.example.trace.auth.dto.DeviceInfo;
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
    @NotBlank
    private Long ProviderId;
    @NotBlank
    private String nickname;
    private String email;
    private String profileImage;
    // Add other necessary fields

    private DeviceInfo deviceInfo;
}
