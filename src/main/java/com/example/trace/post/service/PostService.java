package com.example.trace.post.service;

import com.example.trace.post.dto.PostUpdateDto;
import com.example.trace.post.dto.PostCreateDto;
import com.example.trace.post.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    
    PostDto createPost(PostCreateDto postCreateDto, Long userId);
    
    PostDto getPostById(Long id);
    
    List<PostDto> getPostsByUserId(Long userId);
    
    PostDto updatePost(Long id, PostUpdateDto postUpdateDto, Long userId);
    
    void deletePost(Long id, Long userId);
} 