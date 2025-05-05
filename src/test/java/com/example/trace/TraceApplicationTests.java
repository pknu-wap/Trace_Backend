package com.example.trace;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.test.context.TestPropertySource;

import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.auth.Util.RedisUtil;
import com.example.trace.auth.config.SecurityConfig;
import com.example.trace.auth.repository.UserRepository;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.cloud.openfeign.enabled=false",
		"spring.data.redis.host=localhost",
		"spring.data.redis.port=6379",
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class TraceApplicationTests {

	@MockBean
	private JwtUtil jwtUtil;

	@MockBean
	private RedisUtil redisUtil;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private SecurityConfig securityConfig;

	@Test
	void contextLoads() {
		// Basic test to verify the application context loads successfully
	}

	@Configuration
	static class TestSecurityConfig {
		@Bean
		public WebSecurityCustomizer webSecurityCustomizer() {
			return web -> web.ignoring().requestMatchers("/**");
		}
	}
}