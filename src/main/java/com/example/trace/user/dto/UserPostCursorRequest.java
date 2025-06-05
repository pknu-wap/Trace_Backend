package com.example.trace.user.dto;

import com.example.trace.post.domain.cursor.SearchType;
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
public class UserPostCursorRequest {
    @Schema(description = "커서 날짜 및 시간(첫 요청일 시, null)", example = "null")
    private LocalDateTime cursorDateTime;
    @Schema(description = "커서 ID(첫 요청일 시, null)", example = "null")
    private Long cursorId;
    @Schema(description = "페이지 크기", example = "10")
    private Integer size = 10; // 기본 페이지 크기
    @Schema(description = "마이페이지 탭 타입", example = "WRITTEN_POSTS")
    private MyPageTab myPageTab;

    // 검색을 위한 필드
    @Schema(description = "검색 키워드", example = "")
    private String keyword;
    @Schema(description = "검색 타입", example = "TITLE, CONTENT, ALL")
    private SearchType searchType = SearchType.ALL;
}