package com.example.jwttest.controller;

import com.example.jwttest.Util.ApiResponse;
import com.example.jwttest.converter.UserConverter;
import com.example.jwttest.domain.User;
import com.example.jwttest.dto.AuthResponseDto;
import com.example.jwttest.dto.UserResponseDto;
import com.example.jwttest.service.AuthService;
import com.example.jwttest.dto.JwtDto;
import com.example.jwttest.repository.UserRepository;
import com.example.jwttest.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class AuthController {
    private final AuthService authService;
    @GetMapping("/auth/login/kakao")
    public ApiResponse<AuthResponseDto> kakaoLogin(@RequestParam("code") String accessCode) {
        return ApiResponse.onSuccess(authService.oAuthLogin(accessCode));
    }

}