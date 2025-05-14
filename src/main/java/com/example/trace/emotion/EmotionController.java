package com.example.trace.emotion;

import com.example.trace.emotion.dto.EmotionRequest;
import com.example.trace.emotion.dto.EmotionResponse;
import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emotion")
@RequiredArgsConstructor
@Tag(name = "감정 표현", description = "게시물에 대한 감정 표현 API")
public class EmotionController {

    private final EmotionService emotionServcice;
    @PostMapping()
    @Operation(
            summary = "게시물 감정 표현",
            description = "게시물에 대한 감정 표현을 추가하거나 제거합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "감정 표현 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EmotionResponse.class)
            )
    )
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
