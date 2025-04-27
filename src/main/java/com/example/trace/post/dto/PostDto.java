package com.example.trace.post.dto;

import com.example.trace.post.domain.Post;
import com.example.trace.post.domain.PostImage;
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
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String nickname;
    private String imageUrl;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static PostDto fromEntity(Post post) {
        List<String> imageUrls = post.getImages() != null ? 
                post.getImages().stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList()) : 
                new ArrayList<>();
                
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .imageUrls(imageUrls)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
    

    

    

} 