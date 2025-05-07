package com.example.trace.mission.dailymission;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class DailyMissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    private MissionEntity mission;

    private LocalDate date;


    public DailyMissionEntity() {
    }

    public DailyMissionEntity(MissionEntity mission, LocalDate date) {
        this.mission = mission;
        this.date = date;
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public MissionEntity getMission() {
        return mission;
    }
    public void setMission(MissionEntity mission) {
        this.mission = mission;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
