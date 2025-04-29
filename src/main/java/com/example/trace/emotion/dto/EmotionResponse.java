package com.example.trace.emotion.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmotionResponse {
    private boolean isAdded;
    private String emotionType;
    private Long count;
    public EmotionResponse(boolean isAdded, String emotionType) {
        this.isAdded = isAdded;
        this.emotionType = emotionType;
    }
}
