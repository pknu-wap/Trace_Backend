package com.example.trace.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Schema(description = "미션 커서 요청 DTO")
public class MissionCursorRequest {
    @Schema(description = "커서 날짜 및 시간(첫 요청일 시, null)", example = "null")
    private LocalDateTime cursorDateTime;
    @Schema(description = "커서 ID(첫 요청일 시, null)", example = "null")
    private Integer size;
}
