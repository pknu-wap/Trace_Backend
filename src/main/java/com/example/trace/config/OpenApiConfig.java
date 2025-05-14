package com.example.trace.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path}")
    private String contextPath;
  
    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String appVersion) {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addServersItem(new Server().url(contextPath).description("API 서버"))
                .info(new Info()
                        .title("Trace API")
                        .description("Trace 애플리케이션 API 문서<br><br>" +
                                "API를 테스트하려면:<br>" +
                                "1. id token을 발급받아, /auth/oauth/login api를 통해 로그인하여 jwt 엑세스 토큰을 발급받습니다. <br>" +
                                "2. 오른쪽 상단의 `Authorize` 버튼을 클릭하고 발급받은 JWT 토큰을 입력하세요. (Bearer 키워드 없이)<br>" +
                                "3. 각 API의 'Try it out' 버튼을 클릭하여 필요한 파라미터를 입력하고 테스트할 수 있습니다.")
                        .version(appVersion)
                        .contact(new Contact()
                                .name("Trace Team")
                                .url("https://github.com/pknu-wap/Trace_Backend")
                                .email("wjsbdcindsu@pukyong.ac.kr"))
                        .license(new License()
                                .name("Apache License Version 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT 토큰을 입력해주세요. 'Bearer' 프리픽스 없이 토큰값만 입력하세요.")));
    }
    
    @Bean
    public GroupedOpenApi postApi() {
        return GroupedOpenApi.builder()
                .group("게시글 API")
                .pathsToMatch("/posts/**")
                .build();
    }
    
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("인증 API")
                .pathsToMatch("/auth/**", "/token/**")
                .build();
    }

}