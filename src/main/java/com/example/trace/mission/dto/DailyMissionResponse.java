package com.example.trace.mission.dto;

import com.example.trace.mission.mission.DailyMission;

import java.time.LocalDate;

public class DailyMissionResponse {
    private Long missionId;
    private String content;
    private LocalDate date;

    public DailyMissionResponse() {}

    public DailyMissionResponse(Long missionId, String content, LocalDate date) {
        this.missionId = missionId;
        this.content = content;
        this.date = date;
    }

    public static DailyMissionResponse fromEntity(DailyMission mission) {
        if (mission == null || mission.getMission() == null) {
            throw new IllegalArgumentException("유효하지 않은 미션 데이터입니다.");
        }
        
        return new DailyMissionResponse(
                mission.getMission().getId(),
                mission.getMission().getDescription(),
                mission.getDate()
        );
    }

    public Long getMissionId() {
        return missionId;
    }

    public String getContent() {
        return content;
    }

    public LocalDate getDate() {
        return date;
    }
}
