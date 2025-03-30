package com.example.jwttest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public static CorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 확장성을 위해 다 ArrayList로 처리
        ArrayList<String> allowedOriginPatterns = new ArrayList<>();
        allowedOriginPatterns.add("*");

        ArrayList<String> allowedHttpMethods = new ArrayList<>();
        allowedHttpMethods.add("*");

        configuration.setAllowCredentials(true);   // 내 서버가 응답을 할 때 응답해준 json을 자바스크립트에서 처리할 수 있게 할지를 설정
        configuration.setAllowedOrigins(allowedOriginPatterns); // 응답 허용할 ip
        configuration.addAllowedHeader("*");                    // 응답 허용할 header
        configuration.setAllowedMethods(allowedHttpMethods);    // 응답 허용할 HTTP Method

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // /api/** 로 들어오는 모든 요청들은 config를 따르도록 등록!

        return source;
    }

}