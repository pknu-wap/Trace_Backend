package com.example.trace.post.controller;

import com.example.trace.user.User;
import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.post.dto.PostCreateDto;
import com.example.trace.post.dto.PostDto;
import com.example.trace.post.dto.PostUpdateDto;
import com.example.trace.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto> createPostWithPictures(
            @Valid @RequestPart("request") PostCreateDto postCreateDto,
            @RequestPart(value = "imageFile", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUser().getId();
        String ProviderId = principalDetails.getUser().getProviderId();

        if (imageFiles != null && !imageFiles.isEmpty()) {
            // Limit to 5 images
            int maxImages = Math.min(imageFiles.size(), 5);
            postCreateDto.setImageFiles(imageFiles.subList(0, maxImages));
        }

        PostDto createdPost = postService.createPostWithPictures(postCreateDto, userId, ProviderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long id) {
        PostDto post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateDto postUpdateDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUser().getId();
        PostDto updatedPost = postService.updatePost(id, postUpdateDto, userId);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        Long userId = user.getId();
        postService.deletePost(id, userId);
        return ResponseEntity.noContent().build(); // 삭제 시엔 204 응답
    }
} 