package com.example.trace.post.dto.post;

import com.example.trace.emotion.EmotionType;
import com.example.trace.emotion.dto.EmotionCountDto;
import com.example.trace.post.domain.Post;
import com.example.trace.post.domain.PostImage;
import com.example.trace.post.domain.PostType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 DTO")
public class PostDto {
    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "게시글 타입", example = "FREE, GOOD_DEED, MISSION")
    private PostType postType;

    @Schema(description = "조회수", example = "100")
    private Long viewCount;

    @Schema(description = "감정 수")
    private EmotionCountDto emotionCount;

    @Schema(description = "게시글 제목", example = "게시글 제목")
    private String title;

    @Schema(description = "게시글 내용", example = "게시글 내용")
    private String content;

    @Schema(description = "작성자 providerID", example = "41674...")
    private String providerId;

    @Schema(description = "작성자 닉네임", example = "닉네임")
    private String nickname;

    @Schema(description = "게시글 이미지 URL", example = "[\"image1.jpg\", \"image2.jpg\"]")
    private List<String> imageUrls;

    @Schema(description = "게시글 작성자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "게시글 소유 여부", example = "true")
    @JsonProperty("isOwner")
    private boolean isOwner;

    @Schema(description = "게시글 선행 인증 여부", example = "false")
    @JsonProperty("isVerified")
    private boolean isVerified;

    @Schema(description = "본인이 추가한 감정표현", example = "HEARTWARMING")
    private EmotionType yourEmotionType;

    @Schema(description = "게시글 작성일", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "게시글 수정일", example = "2023-10-01T12:00:00")
    private LocalDateTime updatedAt;
    
    public static PostDto fromEntity(Post post) {
        List<String> imageUrls = post.getImages() != null ? 
                post.getImages().stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList()) : 
                new ArrayList<>();
                
        return PostDto.builder()
                .id(post.getId())
                .postType(post.getPostType())
                .viewCount(post.getViewCount())
                .title(post.getTitle())
                .content(post.getContent())
                .providerId(post.getUser().getProviderId())
                .nickname(post.getUser().getNickname())
                .imageUrls(imageUrls)
                .isVerified(
                        post.getVerification() != null &&
                                (post.getVerification().isImageVerified() ||
                                        post.getVerification().isTextVerified())
                )
                .isOwner(true)
                .profileImageUrl(post.getUser().getProfileImageUrl())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
    

    

    

} 