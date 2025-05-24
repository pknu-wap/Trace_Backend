package com.example.trace.auth;


import com.example.trace.global.errorcode.TokenErrorCode;
import com.example.trace.global.exception.TokenException;
import com.example.trace.user.User;
import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.auth.Util.RedisUtil;
import com.example.trace.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 인증 객체가 필요 없는 경로 패턴을 정의
    private final List<String> excludePathPatterns = List.of(
            "/auth/oauth/**",
            "/h2-console/**",
            "/api/v1/api/user/*",
            "/idtoken",
            "/token/refresh",
            // Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/error"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        //
        return excludePathPatterns.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try{
            log.info("[*] Jwt Filter - Request URI: {}", request.getRequestURI());
            String accessToken = jwtUtil.resolveAccessToken(request);

            // accessToken 없이 접근할 경우
            if (accessToken == null) {
                log.info("[*] No accessToken, proceeding to next filter for URI: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
            // accessToken이 유효하지 않은 경우
            jwtUtil.validateToken(accessToken);

            // logout 혹은 회원탈퇴 처리된 accessToken
            if (redisUtil.get(accessToken) != null && redisUtil.get(accessToken).equals("logout")) {
                log.info("[*] Logout or deleted account accessToken");
                throw new TokenException(TokenErrorCode.LOGOUT_TOKEN);
            }

            if (redisUtil.get(accessToken) != null && redisUtil.get(accessToken).equals("delete")){
                log.info("[*] Deleted account accessToken");
                throw new TokenException(TokenErrorCode.DELETED_USER);
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

        }catch (TokenException e){
            log.error("[*] Jwt Filter - TokenException: {}", e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, e);

        } catch (Exception e){
            log.error("[*] Jwt Filter - Exception: {}", e.getMessage());
        }

    }
}