package com.example.trace.emotion.dto;

import com.example.trace.emotion.EmotionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "감정 표현 요청")
public class EmotionRequest {
    @Schema(description = "감정 표현 타입",
            example = "HEARTWARMING",
            allowableValues = {"HEARTWARMING", "GRATEFUL", "IMPRESSIVE", "TOUCHING", "LIKEABLE"})
    private EmotionType emotionType;
}
