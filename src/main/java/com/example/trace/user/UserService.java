package com.example.trace.user;

import com.example.trace.auth.repository.UserRepository;
import com.example.trace.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * providerId로 사용자 정보를 조회합니다.
     */
    public UserDto getUserInfo(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDto().fromEntity(user);
    }

    /**
     * 모든 사용자 목록을 조회합니다.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
