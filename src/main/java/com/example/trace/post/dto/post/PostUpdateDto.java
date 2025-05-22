package com.example.trace.post.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateDto {
    
    @NotBlank(message = "제목을 입력해주세요")
    private String title;
    
    private String content;
} 