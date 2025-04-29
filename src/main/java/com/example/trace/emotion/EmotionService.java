package com.example.trace.emotion;

import com.example.trace.auth.repository.UserRepository;
import com.example.trace.emotion.dto.EmotionResponse;
import com.example.trace.post.domain.Post;
import com.example.trace.post.repository.PostRepository;
import com.example.trace.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmotionService {
    private final EmotionRepository emotionRepository;
    private final PostRepository postRepository;

    public EmotionResponse toggleEmotion(Long postId,User user, EmotionType emotionType) {
        Optional<Emotion> existingEmotion = emotionRepository
                .findByPostIdAndUserAndEmotionType(postId,user, emotionType);
        if (existingEmotion.isPresent()) {
            // 이미 존재하는 감정표현이면 삭제
            emotionRepository.delete(existingEmotion.get());
            return new EmotionResponse(false, emotionType.name());
        } else {
            // 새로운 감정표현이면 추가
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
            Emotion emotion = Emotion.builder()
                    .post(post)
                    .user(user)
                    .emotionType(emotionType)
                    .build();
            emotionRepository.save(emotion);
            return new EmotionResponse(true, emotionType.name());
        }
    }
}
