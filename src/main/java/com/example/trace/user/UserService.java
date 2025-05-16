package com.example.trace.user;


import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.auth.Util.RedisUtil;
import com.example.trace.auth.repository.UserRepository;
import com.example.trace.global.errorcode.TokenErrorCode;
import com.example.trace.global.exception.TokenException;
import com.example.trace.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public UserDto getUserInfo(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDto().fromEntity(user);
    }

    public void logout(String accessToken) {
        String providerId = jwtUtil.getProviderId(accessToken);
        long expiration = jwtUtil.getExpTime(accessToken);
        redisUtil.save(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
        String redisKey = "RT:" + providerId;
        redisUtil.delete(redisKey);
    }

    public void deleteUser(String accessToken) {
        String providerId = jwtUtil.getProviderId(accessToken);
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new TokenException(TokenErrorCode.NOT_FOUND_USER));
        userRepository.delete(user);
        Long expiration = jwtUtil.getExpTime(accessToken);
        redisUtil.save(accessToken, "delete", expiration, TimeUnit.MILLISECONDS);
        String redisKey = "RT:" + providerId;
        redisUtil.delete(redisKey);
    }
}
