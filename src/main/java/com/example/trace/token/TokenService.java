package com.example.trace.token;

import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.auth.Util.RedisUtil;
import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.auth.dto.TokenResponse;
import com.example.trace.auth.repository.UserRepository;
import com.example.trace.global.errorcode.TokenErrorCode;
import com.example.trace.global.execption.TokenExecption;
import com.example.trace.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        jwtUtil.validateToken(refreshToken);

        // 2. 리프레시 토큰에서 providerId 추출
        String providerId = jwtUtil.getProviderId(refreshToken);

        // 3. Redis에 저장된 리프레시 토큰과 일치하는지 확인
        String redisKey = "RT:" + providerId;
        String storedRefreshToken = (String) redisUtil.get(redisKey);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new TokenExecption(TokenErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        // 4. 사용자 정보 조회
        User user = userRepository.findByProviderIdAndProvider(providerId, "KAKAO")
                .orElseThrow(() -> new TokenExecption(TokenErrorCode.NOT_FOUND_USER));

        // 5. 액세스 토큰 및 리프레시 토큰 재발급
        PrincipalDetails principalDetails = new PrincipalDetails(user);
        String newAccessToken = jwtUtil.createJwtAccessToken(principalDetails);
        String newRefreshToken = jwtUtil.createJwtRefreshToken(principalDetails);

        // 6. 응답 생성
        return new TokenResponse(newAccessToken, newRefreshToken);

    }

    public boolean checkTokenExpiration(String token) {
        jwtUtil.validateToken(token);
        return true;
    }
}
