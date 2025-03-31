package com.example.jwttest.config.jwt;

import com.example.jwttest.PrincipalDetails;
import com.example.jwttest.Util.HttpResponseUtil;
import com.example.jwttest.Util.JwtUtil;
import com.example.jwttest.Util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("[*] Jwt Filter");

        try {
            String accessToken = jwtUtil.resolveAccessToken(request);

            // accessToken 없이 접근할 경우
            if (accessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 유효성 검사
            jwtUtil.validateToken(accessToken);

            // accesstoken을 기반으로 principalDetail 저장
            PrincipalDetails principalDetails = new PrincipalDetails(
                    jwtUtil.getUsername(accessToken),
                    null,
                    jwtUtil.getRoles(accessToken)
            );

            // 스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(
                    principalDetails,
                    null,
                    principalDetails.getAuthorities());

            // 컨텍스트 홀더에 저장
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            try {
                HttpResponseUtil.setErrorResponse(response, HttpStatus.UNAUTHORIZED, null,"엑세스 토큰이 유효하지 않습니다.");
            } catch (IOException ex) {
                log.error("IOException occurred while setting error response: {}", ex.getMessage());
            }
            log.warn("[*] case : accessToken Expired");
        }
    }
}