package com.example.trace.mission.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyMissionRequestDto {
    @NotBlank(message = "사용자 ID는 필수입니다")
    private String providerId;
} 