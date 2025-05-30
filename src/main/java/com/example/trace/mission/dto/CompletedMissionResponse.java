package com.example.trace.mission.dto;

import com.example.trace.mission.mission.CompletedMission;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class CompletedMissionResponse {
    private Long id;
    private Long userId;
    private Long missionId;
    private String missionDescription;
    private Long postId;
    private LocalDateTime completedAt;

    public static CompletedMissionResponse from(CompletedMission completedMission) {
        return CompletedMissionResponse.builder()
                .id(completedMission.getId())
                .userId(completedMission.getUser().getId())
                .missionId(completedMission.getMission().getId())
                .missionDescription(completedMission.getMission().getDescription())
                .postId(completedMission.getPostId())
                .completedAt(completedMission.getCompletedAt())
                .build();
    }
} 