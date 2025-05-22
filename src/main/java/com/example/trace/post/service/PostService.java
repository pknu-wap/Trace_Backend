package com.example.trace.post.service;

import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.post.dto.cursor.CursorResponse;
import com.example.trace.post.dto.cursor.PostCursorRequest;
import com.example.trace.post.dto.post.PostFeedDto;
import com.example.trace.post.dto.post.PostUpdateDto;
import com.example.trace.post.dto.post.PostCreateDto;
import com.example.trace.post.dto.post.PostDto;


public interface PostService {

    PostDto createPost(PostCreateDto postCreateDto,Long userId);

    PostDto createPost(PostCreateDto postCreateDto, String ProviderId, VerificationDto verificationDto);

    PostDto getPostById(Long id,String providerId);

    PostDto updatePost(Long id, PostUpdateDto postUpdateDto,String providerId);
    CursorResponse<PostFeedDto> getAllPostsWithCursor(PostCursorRequest request, String requesterId);
    void deletePost(Long id, String providerId);

} 