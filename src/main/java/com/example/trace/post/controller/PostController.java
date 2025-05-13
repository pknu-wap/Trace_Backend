package com.example.trace.post.controller;

import com.example.trace.user.User;
import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.post.dto.PostCreateDto;
import com.example.trace.post.dto.PostDto;
import com.example.trace.post.dto.PostUpdateDto;
import com.example.trace.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "게시글 API", description = "게시글 관련 API")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "게시글 생성", description = "\"제목, 내용, 이미지 파일을 포함한 게시글을 생성합니다. 최대 5개의 이미지를 업로드할 수 있습니다.\"")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "게시글 생성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PostDto.class),
                examples = {
                    @ExampleObject(
                        name = "게시글 생성 성공",
                        description = "게시글이 성공적으로 생성되었습니다.",
                        value = "{\"id\":1," +
                                "\"title\":\"게시글 제목\"," +
                                "\"content\":\"게시글 내용입니다.\"," +
                                "\"postType\":\"NORMAL\"," +
                                "\"viewCount\":0," +
                                "\"createdAt\":\"2025-05-13T10:00:00\"," +
                                "\"updatedAt\":\"2025-05-13T10:00:00\"," +
                                "\"userId\":1," +
                                "\"imageUrls\":[\"https://example.com/images/1.jpg\",\"https://example.com/images/2.jpg\"]}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "사용자 없음",
                        description = "존재하지 않는 사용자입니다.",
                        value = "{\"code\":\"USER_NOT_FOUND\",\"message\":\"존재하지 않는 사용자입니다.\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "이미지 업로드 실패",
                        description = "게시글 이미지 업로드에 실패했습니다.",
                        value = "{\"code\":\"POST_IMAGE_UPLOAD_FAILED\",\"message\":\"게시글 이미지 업로드에 실패했습니다.\"}"
                    )
                }
            )
        )
    })
    public ResponseEntity<PostDto> createPostWithPictures(
            @Valid @RequestPart("request") PostCreateDto postCreateDto,
            @RequestPart(value = "imageFile", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String ProviderId = principalDetails.getUser().getProviderId();

        if (imageFiles != null && !imageFiles.isEmpty()) {
            // Limit to 5 images
            int maxImages = Math.min(imageFiles.size(), 5);
            postCreateDto.setImageFiles(imageFiles.subList(0, maxImages));
        }

        PostDto createdPost = postService.createPostWithPictures(postCreateDto, ProviderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }



    @GetMapping("/{id}")
    @Operation(
        summary = "게시글 조회",
        description = "특정 ID의 게시글을 조회합니다. 조회 시 조회수가 1 증가합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "게시글 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PostDto.class),
                examples = {
                    @ExampleObject(
                        name = "게시글 조회 성공",
                        description = "게시글을 성공적으로 조회했습니다.",
                        value = "{\"id\":1," +
                                "\"postType\":\"ALL\"," +
                                "\"viewCount\":1," +
                                "\"title\":\"게시글 제목\"," +
                                "\"content\":\"게시글 내용입니다.\"," +
                                "\"providerId\":41469..," +
                                "\"nickname\":\"사용자 닉네임\"," +
                                "\"imageUrls\":[\"https://example.com/images/1.jpg\",\"https://example.com/images/2.jpg\"]}" +
                                "\"createdAt\":\"2025-05-13T10:00:00\"," +
                                "\"updatedAt\":\"2025-05-13T10:00:00\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "게시글 없음",
                        description = "요청한 ID의 게시글을 찾을 수 없습니다.",
                        value = "{\"code\":\"POST_NOT_FOUND\",\"message\":\"게시글을 찾을 수 없습니다.\"}"
                    )
                }
            )
        )
    })
    public ResponseEntity<PostDto> getPost(@PathVariable Long id) {
        PostDto post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "게시글 수정",
        description = "특정 ID의 게시글을 수정합니다. 게시글 작성자만 수정할 수 있습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "게시글 수정 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PostDto.class),
                examples = {
                    @ExampleObject(
                        name = "게시글 수정 성공",
                        description = "게시글을 성공적으로 수정했습니다.",
                        value = "{\"id\":1," +
                        "\"postType\":\"ALL\"," +
                        "\"viewCount\":1," +
                        "\"title\":\"게시글 제목\"," +
                        "\"content\":\"게시글 내용입니다.\"," +
                        "\"providerId\":41469..," +
                        "\"nickname\":\"사용자 닉네임\"," +
                        "\"imageUrls\":[\"https://example.com/images/1.jpg\",\"https://example.com/images/2.jpg\"]}" +
                        "\"createdAt\":\"2025-05-13T10:00:00\"," +
                        "\"updatedAt\":\"2025-05-13T10:30:00\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "게시글 수정 권한 없음",
                        description = "해당 게시글의 수정 권한이 없습니다.",
                        value = "{\"code\":\"POST_UPDATE_FORBIDDEN\",\"message\":\"게시글 수정 권한이 없습니다.\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "게시글 없음",
                        description = "요청한 ID의 게시글을 찾을 수 없습니다.",
                        value = "{\"code\":\"POST_NOT_FOUND\",\"message\":\"게시글을 찾을 수 없습니다.\"}"
                    )
                }
            )
        )
    })
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateDto postUpdateDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String providerId = principalDetails.getUser().getProviderId();
        PostDto updatedPost = postService.updatePost(id, postUpdateDto, providerId);
        return ResponseEntity.ok(updatedPost);
    }



    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "게시글 삭제 성공"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "게시글 삭제 권한 없음",
                        description = "해당 게시글의 삭제 권한이 없습니다.",
                        value = "{\"code\":\"POST_DELETE_FORBIDDEN\",\"message\":\"게시글 수정 권한이 없습니다.\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "게시글 없음",
                        description = "요청한 ID의 게시글을 찾을 수 없습니다.",
                        value = "{\"code\":\"POST_NOT_FOUND\",\"message\":\"게시글을 찾을 수 없습니다.\"}"
                    )
                }
            )
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        String providerId = user.getProviderId();
        postService.deletePost(id, providerId);
        return ResponseEntity.noContent().build();
    }
}