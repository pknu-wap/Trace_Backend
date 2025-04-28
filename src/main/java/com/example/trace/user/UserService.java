package com.example.trace.user;


import com.example.trace.auth.repository.UserRepository;
import com.example.trace.user.dto.UserDto;
import com.example.trace.user.dto.UserInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto getUserInfo(UserInfoRequest userInfoRequest) {
        String providerId = userInfoRequest.getProviderId();
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserDto().fromEntity(user);
    }
}
