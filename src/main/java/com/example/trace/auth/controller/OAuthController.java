package com.example.trace.auth.controller;

import com.example.trace.auth.dto.request.KakaoLoginRequest;
import com.example.trace.auth.dto.request.KakaoSignupRequest;
import com.example.trace.auth.service.KakaoOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
                            examples = {
                                    @ExampleObject(
                                            name = "로그인 성공",
                                            description = "로그인 성공 시 JWT 토큰을 반환합니다.",
                                            value = "{\"accessToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"," +
                                                    "\"refreshToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "202",
                    description = "회원가입 필요",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "회원가입 필요",
                                            description = "회원 가입에 필요한 정보를 제공합니다.",
                                            value = "{\"signupToken\":\"511611e7-8...\"," +
                                                    "\"providerId\":\"4237...\"," +
                                                    "\"email\":\"user@example.com\"," +
                                                    "\"nickname\":\"사용자닉네임\"," +
                                                    "\"profileImage\":\"https://example.com/profile.jpg\"," +
                                                    "\"isRegistered\":false}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "인증 실패",
                                            description = "유효하지 않은 ID 토큰입니다.",
                                            value = "{\"code\":\"INVALID_ID_TOKEN\"," +
                                                    "\"message\":\"잘못된 id_token입니다.\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "서버 오류",
                                            description = "서버 내부 오류입니다.",
                                            value = "{\"code\":\"INTERNAL_SERVER_ERROR\"," +
                                                    "\"message\":\"서버 내부 오류입니다.\"}"
                                    )
                            }
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
                            examples = {
                                    @ExampleObject(
                                            name = "회원가입 성공",
                                            description = "회원가입 성공 시 JWT 토큰을 반환합니다.",
                                            value = "{\"accessToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"," +
                                                    "\"refreshToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "회원가입 세션 없음",
                                            description = "회원 가입 세션이 없거나, providerId가 일치하지 않습니다.",
                                            value = "{\"code\":\"NOT_MATCHED_PROVIDER_ID\"," +
                                                    "\"message\":\"회원 가입 세션이 없거나, providerId가 일치하지 않습니다.\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "서버 오류",
                                            description = "서버 내부 오류입니다.",
                                            value = "{\"code\":\"INTERNAL_SERVER_ERROR\"," +
                                                    "\"message\":\"서버 내부 오류입니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "파일 업로드 오류",
                                            description = "파일 업로드 중 오류가 발생했습니다.",
                                            value = "{\"code\":\"FILE_UPLOAD_ERROR\"," +
                                                    "\"message\":\"파일 업로드 중 오류가 발생했습니다.\"}"
                                    )
                            }
                    )
            )

    })
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