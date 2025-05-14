package com.example.trace.emotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "감정 표현 요청")
public class EmotionRequest {
    @Schema(description = "게시물 ID", example = "1")
    private Long postId;
    @Schema(description = "감정 표현 타입", example = "HEARTWARMING, GRATEFUL, IMPRESSIVE, TOUCHING, LIKABLE")
    private String emotionType;
}
