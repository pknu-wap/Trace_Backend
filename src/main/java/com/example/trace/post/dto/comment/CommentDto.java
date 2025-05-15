package com.example.trace.post.dto.comment;

import com.example.trace.post.domain.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Schema(description = "댓글 DTO")
public class CommentDto {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "댓글 쓴 사람의 providerID", example = "431256341")
    private String providerId;

    @Schema(description = "댓글 ID", example = "1")
    private Long commentId;

    @Schema(description = "부모 댓글 ID", example = "1")
    private Long parentId;

    @Schema(description = "삭제 여부", example = "false")
    @JsonProperty("isDeleted")
    private boolean isDeleted;

    @Schema(description = "댓글 소유 여부", example = "true")
    @JsonProperty("isOwner")
    private boolean isOwner;

    @Schema(description = "댓글 작성자 닉네임", example = "닉네임")
    private String nickName;

    @Schema(description = "댓글 작성자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String userProfileImageUrl;

    @Schema(description = "댓글 내용", example = "댓글 내용")
    private String content;

    @Schema(description = "댓글 작성 시간", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt;


    public static CommentDto fromEntity(Comment comment) {
        return CommentDto.builder()
                .postId(comment.getPost().getId())
                .providerId(comment.getUser().getProviderId())
                .commentId(comment.getId())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .isDeleted(false)
                .isOwner(true)
                .nickName(comment.getUser().getNickname())
                .userProfileImageUrl(comment.getUser().getProfileImageUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }



}
