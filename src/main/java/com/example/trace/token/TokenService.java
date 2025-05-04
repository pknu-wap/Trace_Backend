package com.example.trace.token;

import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.auth.Util.RedisUtil;
import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.auth.dto.TokenResponse;
import com.example.trace.auth.repository.UserRepository;
import com.example.trace.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * 토큰 관리 서비스
 * JWT 토큰의 재발급 및 검증을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Tag(name = "토큰 서비스", description = "JWT 토큰 관리 서비스")
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    /**
     * 리프레시 토큰을 검증하고 새로운 액세스 토큰을 발급합니다.
     * 
     * @param refreshToken 사용자의 리프레시 토큰
     * @return 새로운 액세스 토큰과 리프레시 토큰이 포함된 TokenResponse
     * @throws ResponseStatusException 토큰이 유효하지 않거나, 만료되었거나, 사용자를 찾을 수 없는 경우
     */
    public TokenResponse reissueAccessToken(String refreshToken) {
        try {
            // 1. 리프레시 토큰 유효성 검증
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.");
            }

            // 2. 리프레시 토큰에서 providerId 추출
            String providerId = jwtUtil.getProviderId(refreshToken);

            // 3. Redis에 저장된 리프레시 토큰과 일치하는지 확인
            String redisKey = "RT:" + providerId;
            String storedRefreshToken = (String) redisUtil.get(redisKey);

            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Redis에 저장된 리프레시 토큰과 일치하지 않습니다.");
            }

            // 4. 사용자 정보 조회
            User user = userRepository.findByProviderIdAndProvider(providerId, "KAKAO")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

            // 5. 액세스 토큰 및 리프레시 토큰 재발급
            PrincipalDetails principalDetails = new PrincipalDetails(user);
            String newAccessToken = jwtUtil.createJwtAccessToken(principalDetails);
            String newRefreshToken = jwtUtil.createJwtRefreshToken(principalDetails);

            // 6. 응답 생성
            return new TokenResponse(newAccessToken, newRefreshToken);

        } catch (ExpiredJwtException e) {
            log.error("만료된 리프레시 토큰", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다.");
        } catch (SignatureException | MalformedJwtException e) {
            log.error("유효하지 않은 토큰 서명", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 서명입니다.");
        } catch (Exception e) {
            log.error("토큰 재발급 중 오류 발생", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 재발급 중 오류가 발생했습니다.");
        }
    }

    public boolean checkTokenExpiration(String token) {
        return jwtUtil.validateToken(token);
    }
}
