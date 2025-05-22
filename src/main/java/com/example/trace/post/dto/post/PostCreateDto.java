package com.example.trace.post.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "게시글 작성 요청 DTO")
public class PostCreateDto {

    @NotBlank(message = "게시글 유형을 선택해주세요")
    @Schema(description = "게시글 유형", example = "FREE,GOOD_DEED,MISSION")
    private String postType;
    
    @NotBlank(message = "제목을 입력해주세요")
    @Schema(description = "게시글 제목", example = "게시글 제목")
    private String title;
    @NotBlank(message = "내용을 입력해주세요")
    @Schema(description = "게시글 내용", example = "게시글 내용")
    private String content;

    @JsonIgnore
    private MultipartFile imageFile;

    @JsonIgnore
    private List<MultipartFile> imageFiles;
} 