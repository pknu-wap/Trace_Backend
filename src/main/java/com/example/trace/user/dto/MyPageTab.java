package com.example.trace.user.dto;

public enum MyPageTab {
    WRITTEN_POSTS("나의 흔적"),
    COMMENTED_POSTS("댓글단 글"),
    REACTED_POSTS("반응한 글")
    ;

    private final String description;

    MyPageTab(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
