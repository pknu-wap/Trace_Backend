package com.example.trace.mission.dailymission;

import jakarta.persistence.*;
import com.example.trace.mission.mission.MissionEntity;

@Entity
public class DailyMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 식별 (예: userId)
    private String userId;

    // 미션과의 연관관계
    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    public DailyMission() { }

    public DailyMission(String userId, Mission mission) {
        this.userId = userId;
        this.mission = mission;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Mission getMission() {
        return mission;
    }
    public void setMission(Mission mission) {
        this.mission = mission;
    }
}
