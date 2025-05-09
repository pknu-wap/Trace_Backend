package com.example.trace.post.dto;

import com.example.trace.post.domain.PostType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateDto {

    @NotBlank(message = "게시글 유형을 선택해주세요")
    private String postType;
    
    @NotBlank(message = "제목을 입력해주세요")
    private String title;
    private String content;
    private String imageUrl;

    @JsonIgnore
    private MultipartFile imageFile;

    @JsonIgnore
    private List<MultipartFile> imageFiles;
} 