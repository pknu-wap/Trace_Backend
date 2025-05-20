package com.example.trace.mission.mission;

import com.example.trace.emotion.Emotion;
import com.example.trace.user.User;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class DailyMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    private LocalDate date;

    public DailyMission() {
    }

    public DailyMission(User user, Mission mission, LocalDate date) {
        this.user = user;
        this.mission = mission;
        this.date = date;
    }


    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
