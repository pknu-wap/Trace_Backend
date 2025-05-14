package com.example.trace.gpt.controller;

import com.example.trace.gpt.dto.PostVerificationResult;
import com.example.trace.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verification")
@RequiredArgsConstructor
@Tag(name = "게시글 선행 인증 API", description = "게시글 선행 인증 관련 API")
public class PostVerificationController {

    private final PostService postService;

    @Operation(summary = "게시글 선행 인증", description = "게시글의 선행 인증을 수행합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<PostVerificationResult> verifyPost(@PathVariable Long postId) {
        PostVerificationResult result = postService.verifyPost(postId);
        return ResponseEntity.ok(result);
    }
} 