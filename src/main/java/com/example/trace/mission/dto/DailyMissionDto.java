package com.example.trace.mission.dto;

import java.time.LocalDate;

public class DailyMissionDto {
    private Long id;
    private MissionDto mission;
    private LocalDate date;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public MissionDto getMission() {
        return mission;
    }
    public void setMission(MissionDto mission) {
        this.mission = mission;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
