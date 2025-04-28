package com.example.trace.auth;


import com.example.trace.user.User;
import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.Util.HttpResponseUtil;
import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.auth.Util.RedisUtil;
import com.example.trace.auth.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

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

            // logout 처리된 accessToken
            if (redisUtil.get(accessToken) != null && redisUtil.get(accessToken).equals("logout")) {
                logger.info("[*] Logout accessToken");
                filterChain.doFilter(request, response);
                return;
            }

            // 유효성 검사
            jwtUtil.validateToken(accessToken);

            // accessToken에서 providerId 추출
            String providerId = jwtUtil.getProviderId(accessToken);
            // accessToken에서 providerId로 User 객체를 가져옴
            User user = userRepository.findByProviderIdAndProvider(providerId,"KAKAO").orElseThrow(() -> new UsernameNotFoundException("User not found"));
            // accesstoken을 기반으로 principalDetail 저장
            PrincipalDetails principalDetails = new PrincipalDetails(user);


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