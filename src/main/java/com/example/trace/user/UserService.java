package com.example.trace.user;


import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.auth.Util.RedisUtil;
import com.example.trace.auth.repository.UserRepository;
import com.example.trace.global.errorcode.TokenErrorCode;
import com.example.trace.global.errorcode.UserErrorCode;
import com.example.trace.global.exception.TokenException;
import com.example.trace.global.exception.UserException;
import com.example.trace.user.dto.UpdateNickNameRequest;
import com.example.trace.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    /**
     * providerId로 사용자 정보를 조회합니다.
     */
    public UserDto getUserInfo(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDto.fromEntity(user);
    }

    public User getUser(String providerId){
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(()->new UserException(UserErrorCode.USER_NOT_FOUND));
        return user;
    }


    @Transactional
    public UserDto updateUserNickName(User user,UpdateNickNameRequest request){
        String newNickname = request.getNickname();
        if (newNickname != null) {
            // 닉네임이 현재 닉네임과 다를 때만 중복 체크
            if (!newNickname.equals(user.getNickname()) && userRepository.existsByNickname(newNickname)) {
                throw new UserException(UserErrorCode.ALREADY_IN_USE_NICKNAME);
            }
            user.updateNickname(newNickname);
        }
        userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto updateUserProfileImage(String providerId, String imageUrl) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.updateProfileImageUrl(imageUrl);
        userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
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
