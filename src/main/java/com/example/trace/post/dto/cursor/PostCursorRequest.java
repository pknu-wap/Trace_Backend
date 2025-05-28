package com.example.trace.post.dto.cursor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 커서 요청 DTO")
public class PostCursorRequest {
    @Schema(description = "커서 날짜 및 시간(첫 요청일 시, null)", example = "null")
    private LocalDateTime cursorDateTime;
    @Schema(description = "커서 ID(첫 요청일 시, null)", example = "null")
    private Long cursorId;
    @Schema(description = "페이지 크기", example = "10")
    private Integer size = 10; // 기본 페이지 크기
    @Schema(description = "게시글 타입", example = "MISSION")
    private String postType; // 필터링을 위한 게시글 타입 (선택적)
}