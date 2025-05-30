package com.example.trace.post.dto.cursor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorResponse<T> {
    private List<T> content;
    private boolean hasNext;
    private CursorMeta cursor;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "커서 메타 정보")
    public static class CursorMeta {
        @Schema(description = "커서 날짜 및 시간", example = "2025-05-22T08:26:36.025Z")
        private LocalDateTime dateTime;
        @Schema(description = "커서 ID", example = "71")
        private Long id;
    }
}
