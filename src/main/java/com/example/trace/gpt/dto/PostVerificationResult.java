package com.example.trace.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostVerificationResult {
    private boolean textResult;
    private boolean imageResult;
    private String successReason;
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