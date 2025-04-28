package com.example.trace.controller;

import com.example.trace.dto.KakaoLoginRequest;
import com.example.trace.dto.KakaoSignupRequest;
import com.example.trace.service.KakaoOAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/oauth")
@Slf4j
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody KakaoLoginRequest request) {
        log.info("Received login request with id_token: {}", 
                request.getIdToken().substring(0, Math.min(10, request.getIdToken().length())) + "...");
        return kakaoOAuthService.processLogin(request);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody KakaoSignupRequest request) {
        log.info("Received signup request with id_token: {}", 
                request.getIdToken().substring(0, Math.min(10, request.getIdToken().length())) + "...");
        return kakaoOAuthService.processSignup(request);
    }
}