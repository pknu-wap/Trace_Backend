package com.example.trace.user;

import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<UserDto> getUserInfo(HttpServletRequest request) {
        String token = jwtUtil.resolveAccessToken(request);
        String providerId = jwtUtil.getProviderId(token);
        UserDto userDto = userService.getUserInfo(providerId);
        return ResponseEntity.ok(userDto);
    }
}
