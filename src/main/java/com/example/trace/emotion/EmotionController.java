package com.example.trace.emotion;

import com.example.trace.emotion.dto.EmotionRequest;
import com.example.trace.emotion.dto.EmotionResponse;
import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emotion")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionServcice;
    @PostMapping()
    public EmotionResponse toggleReaction(
            @RequestBody EmotionRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        User user = principalDetails.getUser();
        Long postId = request.getPostId();
        String emotionType = request.getEmotionType();

        EmotionType type = EmotionType.valueOf(emotionType.toUpperCase());

        EmotionResponse emotionResponse = emotionServcice.toggleEmotion(postId,user, type);
        return emotionResponse;
    }

}
