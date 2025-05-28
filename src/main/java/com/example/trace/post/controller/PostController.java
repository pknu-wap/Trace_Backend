package com.example.trace.post.controller;

import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.gpt.service.PostVerificationService;
import com.example.trace.post.dto.cursor.CursorResponse;
import com.example.trace.post.dto.cursor.PostCursorRequest;
import com.example.trace.post.dto.post.PostFeedDto;
import com.example.trace.user.User;
import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.post.dto.post.PostCreateDto;
import com.example.trace.post.dto.post.PostDto;
import com.example.trace.post.dto.post.PostUpdateDto;
import com.example.trace.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "게시글 API", description = "게시글 관련 API")
public class PostController {

    private final PostService postService;
    private final PostVerificationService postVerificationService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "게시글 작성", description = "게시글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "게시글 작성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.class)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
    ))
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestPart("request") PostCreateDto postCreateDto,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String ProviderId = principalDetails.getUser().getProviderId();

        if (imageFiles != null && !imageFiles.isEmpty()) {
            // Limit to 5 images
            int maxImages = Math.min(imageFiles.size(), 5);
            postCreateDto.setImageFiles(imageFiles.subList(0, maxImages));
        }

        PostDto createdPost = postService.createPost(postCreateDto, ProviderId,null);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PostMapping(value ="/verify",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "게시글 작성 시 인증 요구", description = "게시글의 내용이 선행과 관련있는지 인증합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "인증된 게시글 작성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostDto.class)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
    ))
    public ResponseEntity<PostDto> createPostWithVerification(
            @RequestPart("request") PostCreateDto postCreateDto,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String providerId = principalDetails.getUser().getProviderId();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            // Limit to 5 images
            int maxImages = Math.min(imageFiles.size(), 5);
            postCreateDto.setImageFiles(imageFiles.subList(0, maxImages));
        }
        VerificationDto verificationDto = postVerificationService.verifyPost(postCreateDto, providerId);
        PostDto postDto = postService.createPost(postCreateDto,providerId,verificationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postDto);
    }


    @GetMapping("/{id}")
    @Operation(summary = "게시글 조회", description = "게시글을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "게시글 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PostDto.class)
            )
    )
    public ResponseEntity<PostDto> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        PostDto post = postService.getPostById(id,user);
        return ResponseEntity.ok(post);
    }

    @PostMapping("/feed")
    @Operation(summary = "게시글 커서 기반 페이징 조회", description = "커서 기반 페이징으로 게시글을 조회합니다.")
    public ResponseEntity<CursorResponse<PostFeedDto>> getAllPosts(
            @RequestBody PostCursorRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String providerId = principalDetails.getUser().getProviderId();
        CursorResponse<PostFeedDto> response = postService.getAllPostsWithCursor(
                request, providerId != null ? providerId : null);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateDto postUpdateDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String providerId = principalDetails.getUser().getProviderId();
        PostDto updatedPost = postService.updatePost(id, postUpdateDto, providerId);
        return ResponseEntity.ok(updatedPost);
    }




    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponse(
            responseCode = "204",
            description = "게시글 삭제 성공"
    )
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        String providerId = user.getProviderId();
        postService.deletePost(id, providerId);
        return ResponseEntity.noContent().build();
    }
} 