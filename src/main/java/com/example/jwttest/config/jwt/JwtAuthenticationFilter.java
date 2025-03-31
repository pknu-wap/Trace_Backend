package com.example.jwttest.config.jwt;

import com.example.jwttest.PrincipalDetails;
import com.example.jwttest.Util.HttpResponseUtil;
import com.example.jwttest.dto.JwtDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.jwttest.dto.LoginRequestDto;
import com.example.jwttest.Util.JwtUtil;

import java.io.IOException;
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil JwtUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.info("JwtAuthenticationFilter : 로그인 시도 중");

        // request에 있는 username과 password를 파싱해서 자바 Object로 받기
        ObjectMapper om = new ObjectMapper();
        LoginRequestDto loginRequestDto;
        try {
            loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Error of request body.");
        }

        // 유저네임패스워드 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.username(),
                        loginRequestDto.password());

        // PrincipalDetailsService의 loadUserByUsername() 함수가 실행되고 정상이면 authentication이 return됨.
        // Token 넣어서 던져서 인증 끝나면 authentication을 주고, 로그인 한 정보가 담긴다.
        // DB에 있는 username과 password가 일치한다는 뜻
        return authenticationManager.authenticate(authenticationToken);
    }

    // JWT Token 생성해서 response에 담아주기
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        log.info("[*] Login Success! - Login with " + principalDetails.getUsername());
        JwtDto jwtDto = new JwtDto(
                JwtUtil.createJwtAccessToken(principalDetails),
                JwtUtil.createJwtRefreshToken(principalDetails)
        );

        log.info("Access Token: " + jwtDto.accessToken());
        log.info("Refresh Token: " + jwtDto.refreshToken());

        HttpResponseUtil.setSuccessResponse(response, HttpStatus.CREATED, jwtDto);

    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        log.error("[*] Login Failed: " + failed.getMessage());

        // 실패 응답 설정
        HttpResponseUtil.setErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authentication Failed", failed.getMessage());
    }

}