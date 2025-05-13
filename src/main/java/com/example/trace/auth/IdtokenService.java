package com.example.trace.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class IdtokenService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.app.base-url}")
    private String baseUrl;

    public ResponseEntity<?> getTokenWithAuthorizationCode(String authorizationCode) {
        String tokenUri = "https://kauth.kakao.com/oauth/token";
        log.info("Getting token with authorization code. Base URL: {}", baseUrl);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", baseUrl + "/idtoken");
        params.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 토큰 요청
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);
        log.info("Token response from Kakao: {}", response.getBody());

        return response;
    }
}
