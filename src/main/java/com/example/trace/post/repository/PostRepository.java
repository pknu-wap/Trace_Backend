package com.example.trace.post.repository;


import com.example.trace.post.domain.Post;
import com.example.trace.post.domain.PostType;
import com.example.trace.post.dto.post.PostFeedDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    Optional<Post> findById(Long postId);
    List<PostFeedDto> findPostsWithCursor(
            LocalDateTime cursorDateTime,
            Long cursorId,
            int size,
            PostType postType,
            String providerId);


}