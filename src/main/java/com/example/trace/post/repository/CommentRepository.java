package com.example.trace.post.repository;

import com.example.trace.post.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, PostRepositoryCustom {
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);
}
