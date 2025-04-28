package com.example.trace.service;

import com.example.trace.Util.JwtUtil;
import com.example.trace.client.KakaoOAuthClient;
import com.example.trace.domain.User;
import com.example.trace.dto.*;

import com.example.trace.models.OIDCDecodePayload;
import com.example.trace.models.OIDCPublicKey;
import com.example.trace.models.OIDCPublicKeyResponse;
import com.example.trace.repository.UserRepository;
import com.example.trace.provider.KakaoOIDCProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthService {
    private final KakaoOIDCProvider oidcProvider;
    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    @Value("${oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

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

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new SignupRequiredResponse(ProviderId, payload.getEmail(),
                                payload.getNickname(), payload.getPicture()));
            }

        } catch (Exception e) {
            log.error("Error processing login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: " + e.getMessage());
        }
    }

    public ResponseEntity<?> processSignup(KakaoSignupRequest request) {
        try {
            // 1. Get the stored ID token from Redis
            String userId = extractUserIdFromIdToken(request.getIdToken());
            String redisKey = "signup:" + userId;
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

            // 3. Create user with additional info
            User newUser = User.builder()
                    .providerId(payload.getSub())
                    .provider("KAKAO")
                    .email(request.getEmail() != null ? request.getEmail() : payload.getEmail())
                    .nickname(request.getNickname() != null ? request.getNickname() : payload.getNickname())
                    .profileImage(request.getProfileImage() != null ? request.getProfileImage() : payload.getPicture())
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
            String[] parts = idToken.split("\\.");
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            JsonNode payloadNode = new ObjectMapper().readTree(payloadJson);
            return payloadNode.get("sub").asText();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to extract user ID from token", e);
        }
    }

    // These methods would use your JWT implementation
    private String generateAccessToken(User user) {
        PrincipalDetails principalDetails = new PrincipalDetails(user);
        return jwtUtil.createJwtAccessToken(principalDetails);
    }

    private String generateRefreshToken(User user) {
        PrincipalDetails principalDetails = new PrincipalDetails(user);
        return jwtUtil.createJwtRefreshToken(principalDetails);
    }
}