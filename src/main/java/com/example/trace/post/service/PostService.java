package com.example.trace.post.service;

import com.example.trace.gpt.dto.PostVerificationResult;
import com.example.trace.post.dto.PostUpdateDto;
import com.example.trace.post.dto.PostCreateDto;
import com.example.trace.post.dto.PostDto;


public interface PostService {
    
    PostDto createPost(PostCreateDto postCreateDto,Long userId);

    PostDto createPostWithPictures(PostCreateDto postCreateDto, String ProviderId);

    PostDto getPostById(Long id,String providerId);

    PostDto updatePost(Long id, PostUpdateDto postUpdateDto,String providerId);
    
    void deletePost(Long id, String providerId);
    
    PostVerificationResult verifyPost(Long postId);
} 