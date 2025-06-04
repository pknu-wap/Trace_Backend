package com.example.trace.mission.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompletedMissionRequest {
    private Long cursorId; // 커서 정보(선택적, null일 경우 첫 페이지)
}
