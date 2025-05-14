package com.example.trace.auth.Util;

import com.example.trace.auth.dto.PrincipalDetails;

import com.example.trace.global.errorcode.TokenErrorCode;
import com.example.trace.global.exception.TokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ExpiredJwtException;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final Long accessExpMs;
    private final Long refreshExpMs;
    private final RedisUtil redisUtil;
    byte[] decodedKey;
    public JwtUtil(
            // 해당 @Value 값들은 yml에서 설정할 수 있다
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token.access-expiration-time}") Long access,
            @Value("${jwt.token.refresh-expiration-time}") Long refresh,
            RedisUtil redis) {
        decodedKey = Base64.getDecoder().decode(secret);
        if(decodedKey.length != 64) {
            throw new IllegalArgumentException("HS512 requires 64 byte key");
        }
        secretKey = new SecretKeySpec(decodedKey, "HmacSHA512");
        accessExpMs = access;
        refreshExpMs = refresh;
        redisUtil = redis;
        log.info("Decoded Key Length: {} bytes", decodedKey.length);
// HS256: 32 bytes, HS512: 64 bytes 여야 정상
    }

    // JWT 토큰을 입력으로 받아 토큰의 페이로드에서 사용자의 providerId를 추출
    public String getProviderId(String token) throws SignatureException {
        String providerId = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        log.info("[*] providerId: {}", providerId);
        return providerId;
    }

    // JWT 토큰을 입력으로 받아 토큰의 페이로드에서 사용자 이름(roll)을 추출
    public String getRoles(String token) throws SignatureException{
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    // JWT 토큰의 페이로드에서 만료 시간을 검색, 밀리초 단위의 Long 값으로 반환
    public long getExpTime(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration()
                .getTime();
    }


    // Token 발급
    public String tokenProvider(PrincipalDetails principalDetails, Instant expiration) {
        Instant issuedAt = Instant.now();
        String authorities = principalDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(principalDetails.getUser().getProviderId())
                .claim("role", authorities)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    // principalDetails 객체에 대해 새로운 JWT 액세스 토큰을 생성
    public String createJwtAccessToken(PrincipalDetails principalDetails) {
        Instant expiration = Instant.now().plusMillis(accessExpMs);
        return tokenProvider(principalDetails, expiration);
    }

    // principalDetails 객체에 대해 새로운 JWT 리프레시 토큰을 생성
    public String createJwtRefreshToken(PrincipalDetails principalDetails) {
        Instant expiration = Instant.now().plusMillis(refreshExpMs);
        String refreshToken = tokenProvider(principalDetails, expiration);

        // 사용자 providerId를 Redis 키로 사용 (username 대신)
        String redisKey = "RT:" + principalDetails.getUser().getProviderId();
        
        // 레디스에 저장
        redisUtil.save(
                redisKey,
                refreshToken,
                refreshExpMs,
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }

    // HTTP 요청의 'Authorization' 헤더에서 JWT 액세스 토큰을 검색
    public String resolveAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.warn("[*] No Token in req");
            return null;
        }
        log.info("[*] Token exists");

        return authorization.split(" ")[1];
    }
    // 토큰 유효성 검사
    public void validateToken(String token) {
        try{
            Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            log.info("토큰 유효성 검사 중..");
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            throw new TokenException(TokenErrorCode.WRONG_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new TokenException(TokenErrorCode.EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new TokenException(TokenErrorCode.UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new TokenException(TokenErrorCode.ILLEGAL_ARGUMENT);
        } catch (Exception e) {
            log.error("토큰 유효성 검사 중 알 수 없는 오류 발생: {}", e.getMessage());
            throw new TokenException(TokenErrorCode.UNKNOWN_ERROR);
        }
    }

    public boolean checkTokenExpiration(String token) {
        validateToken(token);
        long seconds = 3 *60;
        boolean isExpired = Jwts
                .parser()
                .clockSkewSeconds(seconds)
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
        return isExpired;
    }

}