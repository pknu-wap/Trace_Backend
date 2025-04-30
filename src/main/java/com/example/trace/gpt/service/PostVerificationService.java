package com.example.trace.gpt.service;

import com.example.trace.gpt.dto.PostVerificationResult;
import com.example.trace.post.domain.Post;

public interface PostVerificationService {
    PostVerificationResult verifyPost(Post post);
} 