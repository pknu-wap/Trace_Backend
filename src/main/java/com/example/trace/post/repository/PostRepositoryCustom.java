package com.example.trace.post.repository;

import com.example.trace.post.dto.post.PostFeedDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepositoryCustom {
    List<PostFeedDto> findPostsWithCursor(
            LocalDateTime cursorDateTime,
            Long cursorId,
            int size,
            String postType
    );
}
