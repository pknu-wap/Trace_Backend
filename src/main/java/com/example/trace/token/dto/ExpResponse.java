package com.example.trace.token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description = "토큰 만료 확인 응답")
public class ExpResponse {
    @Schema(description = "토큰 만료 여부", example = "true")
    @JsonProperty("isExpired")
    private boolean isExpired;
}
