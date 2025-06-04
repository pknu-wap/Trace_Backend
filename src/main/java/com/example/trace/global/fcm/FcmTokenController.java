package com.example.trace.global.fcm;

import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "fcm 토큰", description = "fcm 토큰 저장 API")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;


    @PostMapping("/tokens")
    public ResponseEntity<?> saveToken(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody FcmTokenRequest request) {

        String providerId = principalDetails.getUser().getProviderId();
        try {
            fcmTokenService.saveOrUpdateToken(providerId, request.getToken());
            return ResponseEntity.ok(ApiResponse.success("FCM 토큰이 성공적으로 저장되었습니다"));
        } catch (Exception e) {
            log.error("FCM 토큰 저장 실패 - 사용자 ID: {}, 오류: {}", providerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.success("FCM 토큰 저장에 실패했습니다"));
        }
    }
}
