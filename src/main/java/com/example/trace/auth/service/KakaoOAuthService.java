package com.example.trace.auth.service;

import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.auth.client.KakaoOAuthClient;
import com.example.trace.auth.domain.User;
import com.example.trace.auth.dto.*;

import com.example.trace.auth.dto.request.KakaoLoginRequest;
import com.example.trace.auth.dto.request.KakaoSignupRequest;
import com.example.trace.auth.dto.response.SignupRequiredResponse;
import com.example.trace.auth.models.OIDCDecodePayload;
import com.example.trace.auth.models.OIDCPublicKey;
import com.example.trace.auth.models.OIDCPublicKeyResponse;
import com.example.trace.auth.repository.UserRepository;
import com.example.trace.auth.provider.KakaoOIDCProvider;
import com.example.trace.file.FileType;
import com.example.trace.file.S3UploadService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthService {
    private final KakaoOIDCProvider oidcProvider;
    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private final S3UploadService s3UploadService;

    @Value("${oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId; // 카카오 로그인 api의앱 키.

    public ResponseEntity<?> processLogin(KakaoLoginRequest request) {
        try {
            // 1. Validate ID token
            OIDCPublicKeyResponse keyResponse = kakaoOAuthClient.getOIDCPublicKey();
            String kid = oidcProvider.getKidFromUnsignedTokenHeader(request.getIdToken());

            // Find the matching key
            OIDCPublicKey publicKey = keyResponse.getKeys().stream()
                    .filter(key -> kid.equals(key.getKid()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No matching public key found"));

            // Verify signature
            if (oidcProvider.isTokenSignatureInvalid(request.getIdToken(), publicKey)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token signature");
            }

            // Decode and verify payload
            OIDCDecodePayload payload = oidcProvider.verifyAndDecodeToken(request.getIdToken(), kakaoClientId);
            
            // 2. Extract user ID from token payload
            String ProviderId = payload.getSub();
            
            // 3. Check if user exists
            Optional<User> userOpt = userRepository.findByProviderIdAndProvider(ProviderId, "KAKAO");

            if (userOpt.isPresent()) {
                // User exists - login process
                User user = userOpt.get();

                // Generate JWT tokens for your app
                String accessToken = generateAccessToken(user);
                String refreshToken = generateRefreshToken(user);

                return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
            } else {
                // User doesn't exist - store in Redis for signup
                String redisKey = "signup:" + ProviderId;
                redisTemplate.opsForValue().set(redisKey, request.getIdToken());
                redisTemplate.expire(redisKey, 1, TimeUnit.HOURS);

                return ResponseEntity.ok(new SignupRequiredResponse(ProviderId, payload.getEmail(),
                        payload.getNickname(), payload.getPicture()));
            }

        } catch (Exception e) {
            log.error("Error processing login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> processSignup(KakaoSignupRequest request) {
        try {
            // 1. Get the stored ID token from Redis
            String providerId = extractUserIdFromIdToken(request.getIdToken());
            String redisKey = "signup:" + providerId;
            String storedIdToken = redisTemplate.opsForValue().get(redisKey);

            if (storedIdToken == null || !storedIdToken.equals(request.getIdToken())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired signup session");
            }

            // 2. Validate ID token again
            OIDCPublicKeyResponse keyResponse = kakaoOAuthClient.getOIDCPublicKey();
            String kid = oidcProvider.getKidFromUnsignedTokenHeader(request.getIdToken());

            // Find the matching key
            OIDCPublicKey publicKey = keyResponse.getKeys().stream()
                    .filter(key -> kid.equals(key.getKid()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No matching public key found"));

            // Verify signature
            if (oidcProvider.isTokenSignatureInvalid(request.getIdToken(), publicKey)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token signature");
            }

            // Decode and verify payload
            OIDCDecodePayload payload = oidcProvider.verifyAndDecodeToken(request.getIdToken(), kakaoClientId);

            // 회원 가입 요청시, 사진 업로드를 했다면, s3에 저장
            if(request.getProfileImageFile() != null) {
                request.setProfileImageUrl(s3UploadService.saveFile(request.getProfileImageFile(), FileType.PROFILE, payload.getSub()));
            }

            // 3. Create user with additional info
            User newUser = User.builder()
                    .providerId(payload.getSub())
                    .provider("KAKAO")
                    .email(request.getEmail() != null ? request.getEmail() : payload.getEmail())
                    .nickname(request.getNickname() != null ? request.getNickname() : payload.getNickname())
                    .profileImageUrl(request.getProfileImageUrl() != null ? request.getProfileImageUrl() : payload.getPicture()) // 기본 사진은 나중에 구현
                    .role("ROLE_USER")
                    .username(payload.getSub())
                    .build();

            userRepository.save(newUser);

            // 4. Generate JWT tokens for your app
            String accessToken = generateAccessToken(newUser);
            String refreshToken = generateRefreshToken(newUser);

            // 5. Remove the temporary Redis key
            redisTemplate.delete(redisKey);

            return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
        } catch (Exception e) {
            log.error("Error processing signup", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Signup failed: " + e.getMessage());
        }
    }

    private String extractUserIdFromIdToken(String idToken) {
        try {
            // Check if token is null or empty
            if (idToken == null || idToken.trim().isEmpty()) {
                throw new IllegalArgumentException("ID token is null or empty");
            }
            
            // Split the token
            String[] parts = idToken.split("\\.");
            
            // Verify we have at least 2 parts
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid token format: token must have at least 2 parts");
            }
            
            // Add padding if necessary 
            String encodedPayload = parts[1];
            while (encodedPayload.length() % 4 != 0) {
                encodedPayload += "=";
            }
            
            // Decode payload
            String payloadJson = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
            log.debug("Decoded payload: {}", payloadJson);
            
            // Parse JSON and extract user ID
            JsonNode payloadNode = new ObjectMapper().readTree(payloadJson);
            
            // First try "sub" field (standard JWT)
            if (payloadNode.has("sub")) {
                return payloadNode.get("sub").asText();
            } 
            // Then try Kakao-specific ID field
            else if (payloadNode.has("id")) {
                return payloadNode.get("id").asText();
            }
            
            throw new IllegalArgumentException("User ID not found in token payload");
        } catch (IllegalArgumentException e) {
            log.error("Token parsing error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to extract user ID from token", e);
            throw new IllegalArgumentException("Failed to extract user ID from token: " + e.getMessage(), e);
        }
    }

    private String generateAccessToken(User user) {
        PrincipalDetails principalDetails = new PrincipalDetails(user);
        return jwtUtil.createJwtAccessToken(principalDetails);
    }

    private String generateRefreshToken(User user) {
        PrincipalDetails principalDetails = new PrincipalDetails(user);
        return jwtUtil.createJwtRefreshToken(principalDetails);
    }
}