package com.example.trace.mission.dto;

import com.example.trace.mission.mission.DailyMission;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class DailyMissionResponse {
    private String content;
    private int changCount;
    @JsonProperty("isVerified")
    private boolean isVerified;

    public static DailyMissionResponse fromEntity(DailyMission dailyMission) {
        return DailyMissionResponse.builder()
                .content(dailyMission.getMission().getDescription())
                .changCount(dailyMission.getChangeCount())
                .isVerified(dailyMission.isVerified())
                .build();
    }

}
