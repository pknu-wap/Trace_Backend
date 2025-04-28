package com.example.trace.client;

import feign.Logger;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class KakaoOauthConfig {
    
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    
    @Bean
    public Request.Options options() {
        return new Request.Options(
            10, TimeUnit.SECONDS, // Connection timeout
            10, TimeUnit.SECONDS, // Read timeout
            true  // Follow redirects
        );
    }
} 