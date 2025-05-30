package com.example.trace.emotion;

import com.example.trace.emotion.dto.EmotionCountDto;
import com.example.trace.emotion.dto.EmotionResponse;
import com.example.trace.post.domain.Post;
import com.example.trace.post.repository.PostRepository;
import com.example.trace.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmotionService {
    private final EmotionRepository emotionRepository;
    private final PostRepository postRepository;


    public EmotionResponse toggleEmotion(Long postId,User user, EmotionType emotionType) {
        Emotion existingEmotion = emotionRepository.findByPostIdAndUser(postId,user);

        if(existingEmotion == null){
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
        else{
            EmotionType currentType = existingEmotion.getEmotionType();
            if(currentType != emotionType) throw new IllegalStateException("게시글 하나 당 한 종류의 감정표현만 가능합니다.");
            else{
                emotionRepository.delete(existingEmotion);
                return new EmotionResponse(false, emotionType.name());
            }
        }
    }

    public EmotionCountDto getEmotionCountsByType(Long postId) {
        Long heartwarmingCount = emotionRepository.countByPostIdAndEmotionType(postId, EmotionType.HEARTWARMING);
        Long gratefulCount = emotionRepository.countByPostIdAndEmotionType(postId, EmotionType.GRATEFUL);
        Long impressiveCount = emotionRepository.countByPostIdAndEmotionType(postId, EmotionType.IMPRESSIVE);
        Long touchingCount = emotionRepository.countByPostIdAndEmotionType(postId, EmotionType.TOUCHING);
        Long likeableCount = emotionRepository.countByPostIdAndEmotionType(postId, EmotionType.LIKEABLE);

        return EmotionCountDto.builder()
                .heartwarmingCount(heartwarmingCount)
                .gratefulCount(gratefulCount)
                .impressiveCount(impressiveCount)
                .touchingCount(touchingCount)
                .likableCount(likeableCount)
                .build();
    }

    public EmotionType getYourEmotion(Long postId, User user){
        Emotion yourEmotion = emotionRepository.findByPostIdAndUser(postId,user);
        EmotionType yourEmotionType;
        if(yourEmotion == null){
            yourEmotionType = null;
        }
        else {
            yourEmotionType = yourEmotion.getEmotionType();
        }
        return yourEmotionType;
    }
}
