
package com.example.trace.admin.dto.request;

import com.example.trace.admin.dto.DeviceInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoLoginRequest {
    @NotBlank
    private String idToken;

    private DeviceInfo deviceInfo;
}