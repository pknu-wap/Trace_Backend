package com.example.trace.mission.mission;

import com.example.trace.emotion.Emotion;
import com.example.trace.user.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class DailyMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Mission mission;

    private LocalDate date;

    @Column(nullable = false)
    private int changeCount = 0;

    public DailyMission() {
    }

    public DailyMission(User user, Mission mission, LocalDate date) {
        this.user = user;
        this.mission = mission;
        this.date = date;
        this.changeCount = 0;
    }

    public void incrementChangeCount() {
        this.changeCount++;
    }

    public int getChangeCount() {
        return this.changeCount;
    }

    public void setChangeCount(int changeCount) {
        this.changeCount = changeCount;
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
