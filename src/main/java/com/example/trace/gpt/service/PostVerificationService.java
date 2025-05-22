package com.example.trace.gpt.service;

import com.example.trace.gpt.domain.Verification;
import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.post.dto.post.PostCreateDto;

public interface PostVerificationService {
    VerificationDto verifyPost(PostCreateDto postCreateDto,String providerId);
    Verification makeVerification(VerificationDto verificationDto);
} 