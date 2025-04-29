package com.example.trace.emotion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmotionRequest {
    private Long postId;
    private String emotionType;
}
