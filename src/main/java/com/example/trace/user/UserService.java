package com.example.trace.user;


import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.auth.Util.RedisUtil;
import com.example.trace.auth.repository.UserRepository;
import com.example.trace.global.errorcode.TokenErrorCode;
import com.example.trace.global.exception.TokenException;
import com.example.trace.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.trace.user.dto.UpdateUserRequest;

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

    public UserDto updateUserInfo(String providerId, UpdateUserRequest request, String imageUrl) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newNickname = request.getNickname();
        if (newNickname != null) {
            // 닉네임이 현재 닉네임과 다를 때만 중복 체크
            if (!newNickname.equals(user.getNickname()) && userRepository.existsByNickname(newNickname)) {
                throw new IllegalArgumentException("Nickname is already in use.");
            }
            user.updateNickname(newNickname);
        }

        if (imageUrl != null) {
            user.updateProfileImageUrl(imageUrl);
        }

        if (request.getProfileImageUrl() == null && imageUrl == null) {
            // 명시적으로 null이 들어온 경우 → 프로필 사진 제거
            user.updateProfileImageUrl(null);
        } else if (imageUrl != null) {
            // 새 이미지 업로드가 있는 경우 → 새 이미지로 변경
            user.updateProfileImageUrl(imageUrl);
        }

        userRepository.save(user);
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
