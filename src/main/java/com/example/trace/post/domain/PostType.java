package com.example.trace.post.domain;

public enum PostType {
    FREE("자유"),
    GOOD_DEED("선행"),
    MISSION("미션");

    private final String type;
    PostType(String type){
        this.type = type;
    }
    public String getType() {
        return type;
    }
}
