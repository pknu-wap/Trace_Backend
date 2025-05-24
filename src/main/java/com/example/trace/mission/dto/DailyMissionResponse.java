package com.example.trace.mission.dto;

import com.example.trace.mission.mission.DailyMission;

public class DailyMissionResponse {
    private String content;
    private int remainingChanges;

    public DailyMissionResponse() {}

    public DailyMissionResponse(String content, int remainingChanges) {
        this.content = content;
        this.remainingChanges = remainingChanges;
    }

    public static DailyMissionResponse fromEntity(DailyMission mission, int remainingChanges) {
        if (mission == null || mission.getMission() == null) {
            throw new IllegalArgumentException("유효하지 않은 미션 데이터입니다.");
        }
        
        return new DailyMissionResponse(
                mission.getMission().getDescription(),
                remainingChanges
        );
    }

    public String getContent() {
        return content;
    }

    public int getRemainingChanges() {
        return remainingChanges;
    }
}
