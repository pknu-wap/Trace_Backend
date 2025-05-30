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
public class VerificationDto {
    @Schema(description = "텍스트 인증 결과", example = "true")
    private boolean textResult;
    @Schema(description = "이미지 인증 결과", example = "true")
    private boolean imageResult;
    @Schema(description = "인증 성공 이유", example = "텍스트가 선행에 대한 설명이고, 이미지는 텍스트와 관련되있으며 선행과 관련된 이미지입니다.")
    private String successReason;
    @Schema(description = "인증 실패 이유", example = "null")
    private String failureReason;


    public static VerificationDto textOnlyFailure(String reason) {
        return VerificationDto.builder()
                .textResult(false)
                .imageResult(false)
                .failureReason(reason)
                .build();
    }


    public static VerificationDto bothFailure(String reason) {
        return VerificationDto.builder()
                .textResult(false)
                .imageResult(false)
                .failureReason(reason)
                .build();
    }
} 