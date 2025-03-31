package com.example.jwttest.controller;

import com.example.jwttest.Util.ApiResponse;
import com.example.jwttest.dto.JwtDto;
import com.example.jwttest.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IndexController {
    private final AuthService authService;
    @GetMapping("/reissue")
    public ApiResponse<JwtDto> reissueToken(@RequestHeader("RefreshToken") String refreshToken) {
        return ApiResponse.onSuccess(authService.reissueToken(refreshToken));
    }

}
