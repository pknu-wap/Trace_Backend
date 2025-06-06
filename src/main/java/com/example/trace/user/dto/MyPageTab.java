package com.example.trace.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MyPageTab {
    WRITTEN_POSTS("나의 흔적"),
    COMMENTED_POSTS("댓글단 글"),
    REACTED_POSTS("반응한 글")
    ;

    private String description;

    MyPageTab(String description){
        this.description = description;
    }

}
