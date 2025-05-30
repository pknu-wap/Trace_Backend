package com.example.trace.mission.mission;

import com.example.trace.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompletedMission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(name = "post_id")
    private Long postId;

    private LocalDateTime completedAt;

    @Builder
    public CompletedMission(User user, Mission mission) {
        this.user = user;
        this.mission = mission;
        this.completedAt = LocalDateTime.now();
    }

    public void updatePostId(Long postId) {
        this.postId = postId;
    }
} 