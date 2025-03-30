package com.example.jwttest.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter1 implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        res.setCharacterEncoding("utf-8");

        // 만약, token을 검증하여, Controller에 접근 여부 설정!
        if (req.getMethod().equals("POST")) { // request가 POST 메소드라면
            String auth_header = req.getHeader("Authorization"); // 헤더에서 Authorization 값을 가져온다.

            if(auth_header.equals("secret")) {
                filterChain.doFilter(req, res); // 만약에 토큰이 secret 이라면, 필터 이어가게
            } else {
                PrintWriter writer = res.getWriter();
                writer.println("인증 안됨"); // filter 끊기고, Controller의 진입 조차 못하게 막는다.
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}