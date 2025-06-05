package com.example.trace.post.service;

import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.post.dto.cursor.CursorResponse;
import com.example.trace.user.dto.UserPostCursorRequest;
import com.example.trace.post.dto.post.PostFeedDto;
import com.example.trace.post.dto.post.PostUpdateDto;
import com.example.trace.post.dto.post.PostCreateDto;
import com.example.trace.post.dto.post.PostDto;
import com.example.trace.user.User;

public interface PostService {

    PostDto createPost(PostCreateDto postCreateDto,Long userId);

    PostDto createPost(PostCreateDto postCreateDto, String ProviderId, VerificationDto verificationDto);

    PostDto getPostById(Long id, User user);

    PostDto updatePost(Long id, PostUpdateDto postUpdateDto,String providerId);
    
    CursorResponse<PostFeedDto> getAllPostsWithCursor(UserPostCursorRequest request, String requesterId);
    
    void deletePost(Long id, String providerId);

    CursorResponse<PostFeedDto> searchPostsWithCursor(UserPostCursorRequest request, String providerId);

    CursorResponse<PostFeedDto> getMyPagePostsWithCursor(UserPostCursorRequest request, String providerId);
} 