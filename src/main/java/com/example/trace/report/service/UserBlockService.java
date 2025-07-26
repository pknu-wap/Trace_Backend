package com.example.trace.report.service;

import com.example.trace.auth.repository.UserRepository;
import com.example.trace.report.domain.UserBlock;
import com.example.trace.report.dto.BlockedUserResponse;
import com.example.trace.report.repository.UserBlockRepository;
import com.example.trace.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBlockService {

    private final UserRepository userRepository;
    private final UserBlockRepository userBlockRepository;

    public void blockUser(String blockerProviderId, String blockedProviderId) {
        if (blockerProviderId.equals(blockedProviderId)) {
            throw new IllegalArgumentException("자기 자신을 차단할 수 없습니다");
        }
        User blocker = userRepository.findByProviderId(blockerProviderId)
                .orElseThrow(() -> new IllegalArgumentException("차단하는 사용자를 찾을 수 없습니다."));
        User blocked = userRepository.findByProviderId(blockedProviderId)
                .orElseThrow(() -> new IllegalArgumentException("차단될 사용자들 찾을 수 없습니다."));

        userBlockRepository.findByBlockerAndBlocked(blocker, blocked)
                .ifPresent(existingBlock -> {
                    throw new IllegalArgumentException("이미 차단한 사용자입니다.");
                });

        UserBlock userBlock = UserBlock.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();

        userBlockRepository.save(userBlock);
    }

    public void unblockUser(String blockerProviderId, String blockedProviderId) {
        User blocker = userRepository.findByProviderId(blockerProviderId)
                .orElseThrow(() -> new IllegalArgumentException("차단 해제하는 사용자를 찾을 수 없습니다."));
        User blocked = userRepository.findByProviderId(blockedProviderId)
                .orElseThrow(() -> new IllegalArgumentException("차단 해제될 사용자를 찾을 수 없습니다."));

        UserBlock userBlock = userBlockRepository.findByBlockerAndBlocked(blocker, blocked)
                .orElseThrow(() -> new IllegalArgumentException("차단된 사용자가 아닙니다."));

        userBlockRepository.delete(userBlock);
    }

    @Transactional(readOnly = true)
    public List<BlockedUserResponse> getBlockedUsers(String blockerProviderId) {
        User blocker = userRepository.findByProviderId(blockerProviderId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<UserBlock> userBlocks = userBlockRepository.findAllByBlocker(blocker);

        return userBlocks.stream()
                .map(BlockedUserResponse::fromEntity)
                .toList();
    }

}
