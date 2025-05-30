package com.example.trace.emotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Schema(description = "감정 수 DTO")
public class EmotionCountDto {
    @Schema(description = "따뜻해요 개수", example = "5")
    private Long heartwarmingCount;

    @Schema(description = "고마워요 개수", example = "3")
    private Long gratefulCount;

    @Schema(description = "멋져요 개수", example = "7")
    private Long impressiveCount;

    @Schema(description = "감동이에요 개수", example = "2")
    private Long touchingCount;

    @Schema(description = "좋아요 개수", example = "10")
    private Long likeableCount;
}
