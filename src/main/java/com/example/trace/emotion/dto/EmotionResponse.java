package com.example.trace.emotion.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "감정 표현 응답 DTO")
public class EmotionResponse {
    @Schema(description = "감정 표현 추가 여부", example = "true")
    private boolean isAdded;
    @Schema(description = "감정 표현 타입", example = "LIKE")
    private String emotionType;
    public EmotionResponse(boolean isAdded, String emotionType) {
        this.isAdded = isAdded;
        this.emotionType = emotionType;
    }
}
