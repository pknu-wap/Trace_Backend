package com.example.trace.emotion;

public enum EmotionType {
    WARM("따뜻해요"),
    GRATEFUL("고마워요"),
    AWESOME("멋져요"),
    TOUCHED("감동이에요"),
    LIKE("좋아요");

    private final String description;

    EmotionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}