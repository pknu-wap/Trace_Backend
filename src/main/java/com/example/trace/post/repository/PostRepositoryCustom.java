package com.example.trace.post.repository;

import com.example.trace.post.dto.comment.CommentDto;
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

    List<CommentDto> findComments(
            List<Long> parentCommentIds,
            String providerId
    );
    List<Long> findParentCommentsIdWithCursor(LocalDateTime cursorDateTime,
                                                    Long cursorId,
                                                    Long postId,
                                                    int size,
                                                    String providerId
    );
}
