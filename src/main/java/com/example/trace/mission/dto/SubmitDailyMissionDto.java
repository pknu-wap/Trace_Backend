package com.example.trace.mission.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SubmitDailyMissionDto {
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
