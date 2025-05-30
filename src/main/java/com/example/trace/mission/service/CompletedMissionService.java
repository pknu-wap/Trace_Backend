package com.example.trace.mission.service;

import com.example.trace.mission.mission.CompletedMission;
import com.example.trace.mission.dto.CompletedMissionResponse;
import com.example.trace.mission.repository.CompletedMissionRepository;
import com.example.trace.user.User;
import com.example.trace.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompletedMissionService {

    private final CompletedMissionRepository completedMissionRepository;
    private final UserRepository userRepository;
    
    private static final int DEFAULT_PAGE_SIZE = 20;

    public List<CompletedMissionResponse> getUserCompletedMissions(String providerId, Long cursorId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<CompletedMission> completedMissions = completedMissionRepository
                .findByUserWithCursor(user, cursorId, DEFAULT_PAGE_SIZE);

        return completedMissions.stream()
                .map(CompletedMissionResponse::from)
                .collect(Collectors.toList());
    }
} 