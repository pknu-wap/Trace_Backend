package com.example.trace.emotion;

public enum EmotionType {
    HEARTWARMING("따뜻해요"),
    GRATEFUL("고마워요"),
    IMPRESSIVE("멋져요"),
    TOUCHING("감동이에요"),
    LIKEABLE("좋아요");

    private final String description;

    EmotionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}