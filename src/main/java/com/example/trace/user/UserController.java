package com.example.trace.user;

import com.example.trace.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> getUserInfo(@RequestBody  UserInfoRequest userInfoRequest) {
        UserDto userDto = userService.getUserInfo(userInfoRequest);
        return ResponseEntity.ok(userDto);
    }
}
