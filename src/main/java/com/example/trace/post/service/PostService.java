package com.example.trace.post.service;

import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.post.dto.PostUpdateDto;
import com.example.trace.post.dto.PostCreateDto;
import com.example.trace.post.dto.PostDto;


public interface PostService {

    PostDto createPost(PostCreateDto postCreateDto,Long userId);

    PostDto createPost(PostCreateDto postCreateDto, String ProviderId, VerificationDto verificationDto);

    PostDto getPostById(Long id,String providerId);

    PostDto updatePost(Long id, PostUpdateDto postUpdateDto,String providerId);
    
    void deletePost(Long id, String providerId);

} 