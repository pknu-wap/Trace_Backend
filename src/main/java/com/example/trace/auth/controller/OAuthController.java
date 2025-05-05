package com.example.trace.auth.controller;

import com.example.trace.auth.dto.request.KakaoLoginRequest;
import com.example.trace.auth.dto.request.KakaoSignupRequest;
import com.example.trace.auth.dto.response.AuthResponse;
import com.example.trace.auth.service.KakaoOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/oauth")
@Slf4j
@Tag(name = "OAuth 인증", description = "카카오 OAuth 로그인 및 회원가입 관련 API")
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;


    @Operation(
            summary = "카카오 로그인",
            description = "카카오 ID 토큰을 사용하여 로그인합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공 - 토큰 발급됨",
                    content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "202",
                    description = "회원가입 필요 - 추가 정보 제공 필요",
                    content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 잘못된 ID 토큰",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 로그인 처리 중 오류 발생",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody KakaoLoginRequest request) {
        log.info("Received login request with id_token: {}",
                request.getIdToken().substring(0, Math.min(10, request.getIdToken().length())) + "...");
        return kakaoOAuthService.processLogin(request);
    }

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signup(
            @RequestPart("request") KakaoSignupRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        log.info("Received signup request with user_id: {}",
                request.getProviderId().substring(0, Math.min(10, request.getProviderId().length())) + "...");

        if (profileImage != null) {
            request.setProfileImageFile(profileImage);
        }

        return kakaoOAuthService.processSignup(request);
    }
}