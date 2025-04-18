package com.example.trace.auth.controller;

import com.example.trace.auth.dto.request.KakaoLoginRequest;
import com.example.trace.auth.dto.request.KakaoSignupRequest;
import com.example.trace.auth.service.KakaoOAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/oauth")
@Slf4j
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;

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
        log.info("Received signup request with id_token: {}",
                request.getIdToken().substring(0, Math.min(10, request.getIdToken().length())) + "...");

        if (profileImage != null) {
            request.setProfileImageFile(profileImage);
        }

        
        return kakaoOAuthService.processSignup(request);
    }
}