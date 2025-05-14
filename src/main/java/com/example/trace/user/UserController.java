package com.example.trace.user;

import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User", description = "사용자 정보 API")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

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
}
