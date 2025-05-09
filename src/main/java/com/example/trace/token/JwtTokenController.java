package com.example.trace.token;

import com.example.trace.auth.dto.TokenResponse;
import com.example.trace.token.dto.ExpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "토큰 관리", description = "JWT 토큰 재발급 및 관리 API")
public class JwtTokenController {

    private final TokenService tokenService;

    @Operation(
        summary = "액세스 토큰 재발급",
        description = "유효한 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "토큰 재발급 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "유효하지 않은 리프레시 토큰 또는 만료된 토큰",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "사용자를 찾을 수 없음",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "서버 오류",
            content = @Content
        )
    })
    @GetMapping("/refresh")
    public ResponseEntity<TokenResponse> reissueAccessToken(
        @Parameter(description = "유효한 리프레시 토큰", required = true) 
        @RequestParam String refreshToken
    ) {
        log.info("[*] Access Token 재발급 요청 - refreshToken: {}", refreshToken);
        TokenResponse response = tokenService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "토큰 만료 여부 확인",
        description = "JWT 토큰의 만료 여부를 확인합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "토큰 상태 확인 성공",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "서버 오류",
            content = @Content
        )
    })
    @GetMapping("/expiration")
    public ResponseEntity<ExpResponse> checkTokenExpiration(
        @Parameter(description = "확인할 JWT 토큰", required = true) 
        @RequestParam String token
    ) {
        log.info("[*] 토큰 만료 확인 요청 - token: {}", token);
        boolean isExpired = tokenService.checkTokenExpiration(token);
        ExpResponse response = new ExpResponse(isExpired);
        return ResponseEntity.ok(response);
    }
}
