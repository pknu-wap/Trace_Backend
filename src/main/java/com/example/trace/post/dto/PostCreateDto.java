package com.example.trace.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateDto {
    
    @NotBlank(message = "제목을 입력해주세요")
    private String title;
    
    private String content;
} 