package com.example.trace.user;

import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.auth.Util.RedisUtil;
import com.example.trace.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User", description = "사용자 정보 API")
@Slf4j
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Operation(summary = "유저 정보 조회", description = "유저 정보를 가져옵니다.")
    @ApiResponse(
            responseCode = "200",
            description = "User information retrieved successfully.",
            content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = UserDto.class)
            )
    )
    @PostMapping
    public ResponseEntity<UserDto> getUserInfo(HttpServletRequest request) {
        String token = jwtUtil.resolveAccessToken(request);
        String providerId = jwtUtil.getProviderId(token);
        UserDto userDto = userService.getUserInfo(providerId);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String accessToken = jwtUtil.resolveAccessToken(request);
        if (accessToken != null) {
            // Get the token's remaining expiration time
            long expiration = jwtUtil.getExpTime(accessToken);
            // Add token to blacklist in Redis with "logout" value and expiration time
            redisUtil.save(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
            String test = (String)redisUtil.get(accessToken);
            log.info("Logout test: {}", test);
            // Also delete the refresh token for this user
            String providerId = jwtUtil.getProviderId(accessToken);
            String redisKey = "RT:" + providerId;
            redisUtil.delete(redisKey);
        }
        return ResponseEntity.ok().build();
    }
}
