package com.example.trace.auth.controller;

import com.example.trace.auth.dto.TokenResponse;
import com.example.trace.auth.dto.request.KakaoLoginRequest;
import com.example.trace.auth.dto.request.KakaoSignupRequest;
import com.example.trace.auth.dto.response.SignupRequiredResponse;
import com.example.trace.auth.service.KakaoOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
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
@Tag(name = "OAuth", description = "OAuth 관련 API")
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;


    @Operation(
            summary = "카카오 로그인",
            description = "카카오 로그인 요청을 처리합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "202",
                    description = "회원가입 필요",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignupRequiredResponse.class)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody KakaoLoginRequest request) {
        log.info("Received login request with id_token: {}",
                request.getIdToken().substring(0, Math.min(10, request.getIdToken().length())) + "...");
        return kakaoOAuthService.processLogin(request);
    }



    @Operation(
            summary = "회원가입",
            description = "회원가입 요청을 처리합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class)
                    )
            )

    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
    ))
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