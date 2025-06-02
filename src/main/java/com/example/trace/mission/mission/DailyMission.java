package com.example.trace.mission.mission;

import com.example.trace.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
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
    private int changeCount;

    @Column(name="is_verified")
    private boolean isVerified;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "post_id")
    private Long postId;

    public void changeMission(Mission newMission) {
        this.mission = newMission;
        this.changeCount++;
    }

    public void updateVerification(boolean isVerified) {
        this.isVerified = isVerified;
        if (isVerified) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public void updatePostId(Long postId) {
        this.postId = postId;
    }
}
