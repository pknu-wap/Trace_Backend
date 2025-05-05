package com.example.trace.auth.service;

import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.auth.client.KakaoOAuthClient;
import com.example.trace.auth.dto.response.AuthResponse;
import com.example.trace.user.User;
import com.example.trace.auth.dto.*;

import com.example.trace.auth.dto.request.KakaoLoginRequest;
import com.example.trace.auth.dto.request.KakaoSignupRequest;
import com.example.trace.auth.models.OIDCDecodePayload;
import com.example.trace.auth.models.OIDCPublicKey;
import com.example.trace.auth.models.OIDCPublicKeyResponse;
import com.example.trace.auth.repository.UserRepository;
import com.example.trace.auth.provider.KakaoOIDCProvider;
import com.example.trace.file.FileType;
import com.example.trace.file.S3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
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

                return ResponseEntity.status(HttpStatus.OK).body(AuthResponse.loginSuccess(accessToken, refreshToken));
            } else {
                // 사용자 없으면 레디스에 임시 회원가입 토큰 저장
                String signupToken = UUID.randomUUID().toString();
                redisTemplate.opsForValue().set("signup:" + signupToken, ProviderId, 1, TimeUnit.HOURS);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(AuthResponse.signupRequired(signupToken,ProviderId,payload.getEmail(), payload.getNickname(), payload.getPicture()));
            }

        } catch (Exception e) {
            log.error("Error processing login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> processSignup(KakaoSignupRequest request) {
        try {
            // 레디스에 저장된 providerId와 요청의 providerId를 비교
            String redisKey = "signup:" + request.getSignupToken();
            String storedProviderId = redisTemplate.opsForValue().get(redisKey);
            if (storedProviderId == null || !storedProviderId.equals(request.getProviderId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired signup session");
            }

            // 회원 가입 요청시, 사진 업로드를 했다면, s3에 저장
            if(request.getProfileImageFile() != null) {
                request.setProfileImageUrl(s3UploadService.saveFile(request.getProfileImageFile(), FileType.PROFILE,request.getProviderId() ));
            }

            // user 생성
            User newUser = User.builder()
                    .providerId(request.getProviderId())
                    .provider("KAKAO")
                    .email(request.getEmail() != null ? request.getEmail() : null)
                    .nickname(request.getNickname() != null ? request.getNickname() : null)
                    .profileImageUrl(request.getProfileImageUrl() != null ? request.getProfileImageUrl() : null) // 기본 사진은 나중에 구현
                    .role("ROLE_USER")
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



    private String generateAccessToken(User user) {
        PrincipalDetails principalDetails = new PrincipalDetails(user);
        return jwtUtil.createJwtAccessToken(principalDetails);
    }

    private String generateRefreshToken(User user) {
        PrincipalDetails principalDetails = new PrincipalDetails(user);
        return jwtUtil.createJwtRefreshToken(principalDetails);
    }
}