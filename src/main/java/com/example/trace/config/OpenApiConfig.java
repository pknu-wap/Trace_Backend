package com.example.trace.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
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
                        .description("Trace 애플리케이션 API 문서")
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
                                .description("JWT 토큰을 입력해주세요.")));
    }
}