package com.example.trace.post.repository;

import com.example.trace.post.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPostId(Long postId);
    void deleteByPostId(Long postId);
} 