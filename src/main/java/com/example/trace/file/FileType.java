package com.example.trace.file;

public enum FileType {
    PROFILE("profile/"),
    POST("post/");

    private final String path;

    FileType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}