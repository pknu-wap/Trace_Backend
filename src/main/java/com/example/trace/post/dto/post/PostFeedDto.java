package com.example.trace.post.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 피드 DTO")
public class PostFeedDto {
    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "게시글 타입", example = "MISSION")
    private String postType;

    @Schema(description = "게시글 제목", example = "제목")
    private String title;

    @Schema(description = "게시글 내용", example = "내용")
    private String content;

    @Schema(description = "providerId", example = "41564..")
    private String providerId;

    @Schema(description = "닉네임", example = "원지섭")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "게시글 이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "게시글 조회수", example = "100")
    private Long viewCount;

    @Schema(description = "게시글 댓글 수", example = "50")
    private Long commentCount;

    @Schema(description = "생성 시간", example = "2025-05-22T08:29:59.687Z")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간", example = "2025-05-22T08:29:59.687Z")
    private LocalDateTime updatedAt;

    @Schema(description = "인증 여부", example = "false")
    private boolean isVerified;
}
