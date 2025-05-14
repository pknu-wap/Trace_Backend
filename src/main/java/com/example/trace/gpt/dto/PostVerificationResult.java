package com.example.trace.gpt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시물 선행 인증 결과")
public class PostVerificationResult {
    @Schema(description = "텍스트 인증 결과", example = "true")
    private boolean textResult;
    @Schema(description = "이미지 인증 결과", example = "true")
    private boolean imageResult;
    @Schema(description = "인증 성공 이유", example = "텍스트 인증 성공")
    private String successReason;
    @Schema(description = "인증 실패 이유", example = "텍스트 인증 실패")
    private String failureReason;


    public static PostVerificationResult textOnlyFailure(String reason) {
        return PostVerificationResult.builder()
                .textResult(false)
                .imageResult(false)
                .failureReason(reason)
                .build();
    }


    public static PostVerificationResult bothFailure(String reason) {
        return PostVerificationResult.builder()
                .textResult(false)
                .imageResult(false)
                .failureReason(reason)
                .build();
    }
} 