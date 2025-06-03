package com.example.trace.post.repository;

import com.example.trace.post.domain.PostType;
import com.example.trace.post.domain.cursor.SearchType;
import com.example.trace.post.dto.comment.CommentDto;
import com.example.trace.post.dto.post.PostFeedDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepositoryCustom {
    List<PostFeedDto> findPostsWithCursor(
            LocalDateTime cursorDateTime,
            Long cursorId,
            int size,
            PostType postType,
            String providerId
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

    List<PostFeedDto> findPostsWithCursorAndSearch(
            LocalDateTime cursorDateTime,
            Long cursorId,
            int size,
            PostType postType,
            String keyword,
            SearchType searchType,
            String providerId
    );

    List<PostFeedDto> findUserPosts(
            String providerId,
            LocalDateTime cursorDateTime,
            Long cursorId,
            int size
    );

    List<PostFeedDto> findUserCommentedPosts(
            String providerId,
            LocalDateTime cursorDateTime,
            Long cursorId,
            int size
    );

    List<PostFeedDto> findUserEmotedPosts(
            String providerId,
            LocalDateTime cursorDateTime,
            Long cursorId,
            int size
    );
}
