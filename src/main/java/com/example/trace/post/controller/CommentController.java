package com.example.trace.post.controller;

import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.post.dto.comment.CommentCreateDto;
import com.example.trace.post.dto.comment.CommentDto;
import com.example.trace.post.dto.cursor.CommentCursorRequest;
import com.example.trace.post.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Tag(name = "Comment", description = "댓글 API")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "댓글 작성 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CommentDto.class)
            )
    )
    @PostMapping("/{postId}")
    public ResponseEntity<?> addComment(
            @PathVariable Long postId,
            @RequestBody CommentCreateDto commentCreateDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String providerId = principalDetails.getUser().getProviderId();
        return ResponseEntity.ok(commentService.addComment(postId, commentCreateDto, providerId));
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @ApiResponse(
            responseCode = "204",
            description = "댓글 삭제 성공"
    )
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String providerId = principalDetails.getUser().getProviderId();
        CommentDto commentDto = commentService.deleteComment(commentId, providerId);
        return ResponseEntity.ok(commentDto);
    }

    @Operation(summary = "대댓글 작성", description = "게시글에 대댓글을 작성합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "대댓글 작성 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CommentDto.class)
            )
    )
    @PostMapping("/{postId}/{commentId}")
    public ResponseEntity<?> addChildrenComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentCreateDto commentCreateDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String providerId = principalDetails.getUser().getProviderId();
        return ResponseEntity.ok(commentService.addChildrenComment(postId,commentId, commentCreateDto, providerId));
    }


    @PostMapping("/{postId}/cursor")
    public ResponseEntity<?> getCommentList(
            @PathVariable Long postId,
            @RequestBody CommentCursorRequest commentCursorRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String providerId = principalDetails.getUser().getProviderId();
        return ResponseEntity.ok(commentService.getCommentsWithCursor(commentCursorRequest,postId, providerId));
    }


}
