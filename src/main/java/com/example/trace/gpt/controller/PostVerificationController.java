package com.example.trace.gpt.controller;

import com.example.trace.gpt.dto.PostVerificationResult;
import com.example.trace.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verification")
@RequiredArgsConstructor
public class PostVerificationController {

    private final PostService postService;
    
    @GetMapping("/{postId}")
    public ResponseEntity<PostVerificationResult> verifyPost(@PathVariable Long postId) {
        PostVerificationResult result = postService.verifyPost(postId);
        return ResponseEntity.ok(result);
    }
} 