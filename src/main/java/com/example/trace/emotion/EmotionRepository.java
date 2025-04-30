package com.example.trace.emotion;

import com.example.trace.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    Optional<Emotion> findByPostIdAndUserAndEmotionType(Long postId, User user, EmotionType emotionType);

}
