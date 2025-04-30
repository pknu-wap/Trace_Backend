package com.example.trace.post.service;

import com.example.trace.gpt.dto.PostVerificationResult;
import com.example.trace.post.dto.PostUpdateDto;
import com.example.trace.post.dto.PostCreateDto;
import com.example.trace.post.dto.PostDto;


public interface PostService {
    
    PostDto createPost(PostCreateDto postCreateDto,Long userId);

    PostDto createPostWithPictures(PostCreateDto postCreateDto,Long userId, String ProviderId);

    PostDto getPostById(Long id);

    PostDto updatePost(Long id, PostUpdateDto postUpdateDto,Long userId);
    
    void deletePost(Long id, Long userId);
    
    PostVerificationResult verifyPost(Long postId);
} 