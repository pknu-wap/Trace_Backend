package com.example.trace.token;

import com.example.trace.auth.dto.TokenResponse;
import com.example.trace.token.dto.CheckExpRequest;
import com.example.trace.token.dto.ExpResponse;
import com.example.trace.token.dto.ReIssueRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "토큰 재발급 성공",
                        description = "새로운 액세스 토큰과 리프레시 토큰이 발급되었습니다.",
                        value = "{\"accessToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\"refreshToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "만료된 토큰",
                        description = "만료된 JWT 토큰입니다.",
                        value = "{\"code\":\"EXPIRED_JWT_TOKEN\",\"message\":\"만료된 JWT 토큰입니다.\",\"isExpired\":true,\"isValid\":false}"
                    ),
                    @ExampleObject(
                        name = "잘못된 서명",
                        description = "잘못된 JWT 서명입니다.",
                        value = "{\"code\":\"WRONG_SIGNATURE\",\"message\":\"잘못된 서명입니다.\",\"isExpired\":false,\"isValid\":false}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "지원되지 않는 토큰",
                        description = "지원되지 않는 JWT 토큰 형식입니다.",
                        value = "{\"code\":\"UNSUPPORTED_JWT_TOKEN\",\"message\":\"지원되지 않는 JWT 토큰입니다.\",\"isExpired\":false,\"isValid\":false}"
                    ),
                    @ExampleObject(
                        name = "지원되지 않는 토큰",
                        description = "JWT 토큰이 아닙니다.",
                        value = "{\"code\":\"ILLEGAL_ARGUMENT\",\"message\":\"JWT 토큰이 아닙니다.\",\"isExpired\":false,\"isValid\":false}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "리소스를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "리프레시 토큰 없음",
                        description = "리프레시 토큰이 존재하지 않거나 일치하지 않습니다.",
                        value = "{\"code\":\"NOT_FOUND_REFRESH_TOKEN\",\"message\":\"리프레시 토큰이 존재하지 않거나, 일치하지 않습니다.\",\"isExpired\":false,\"isValid\":true}"
                    ),
                    @ExampleObject(
                        name = "사용자 없음",
                        description = "사용자가 존재하지 않습니다.",
                        value = "{\"code\":\"NOT_FOUND_USER\",\"message\":\"사용자가 존재하지 않습니다\",\"isExpired\":false,\"isValid\":true}"
                    )
                }
            )
        )
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> reissueAccessToken(
        @Parameter(description = "유효한 리프레시 토큰", required = true) 
        @RequestBody ReIssueRequest request
    ) {
        String refreshToken = request.getRefreshToken();
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
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "토큰 만료됨",
                        description = "토큰이 만료되었습니다.",
                        value = "{\"isExpired\": true}"
                    ),
                    @ExampleObject(
                        name = "토큰 유효함",
                        description = "토큰이 아직 유효합니다.",
                        value = "{\"isExpired\": false}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "잘못된 서명",
                        description = "잘못된 JWT 서명입니다.",
                        value = "{\"code\":\"WRONG_SIGNATURE\",\"message\":\"잘못된 서명입니다.\",\"isExpired\":false,\"isValid\":false}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "지원되지 않는 토큰",
                        description = "지원되지 않는 JWT 토큰 형식입니다.",
                        value = "{\"code\":\"UNSUPPORTED_JWT_TOKEN\",\"message\":\"지원되지 않는 JWT 토큰입니다.\",\"isExpired\":false,\"isValid\":false}"
                    ),
                    @ExampleObject(
                        name = "유효하지 않은 토큰",
                        description = "JWT 토큰이 아닙니다.",
                        value = "{\"code\":\"ILLEGAL_ARGUMENT\",\"message\":\"JWT 토큰이 아닙니다.\",\"isExpired\":false,\"isValid\":false}"
                    )
                }
            )
        )
    })
    @PostMapping("/expiration")
    public ResponseEntity<ExpResponse> checkTokenExpiration(
            @Parameter(description = "확인할 JWT 토큰", required = true)
            @RequestBody CheckExpRequest request
    ) {
        String token = request.getToken();
        log.info("[*] 토큰 만료 확인 요청 - token: {}", token);
        boolean isExpired = tokenService.checkTokenExpiration(token);
        ExpResponse response = new ExpResponse(isExpired);
        return ResponseEntity.ok(response);
    }
}
