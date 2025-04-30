package com.example.trace.gpt.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class OpenAIConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.timeout:60}")
    private int timeout;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(openaiApiKey, Duration.ofSeconds(timeout));
    }
} 