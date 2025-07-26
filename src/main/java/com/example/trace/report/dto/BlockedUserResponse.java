package com.example.trace.report.dto;

import com.example.trace.report.domain.UserBlock;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "차단한 사용자 정보")
public class BlockedUserResponse {

    @Schema(description = "차단한 사용자 닉네임")
    private String nickname;

    @Schema(description = "차단 일시")
    private LocalDateTime blockedAt;

    public static BlockedUserResponse fromEntity(UserBlock userBlock) {
        return BlockedUserResponse.builder()
                .nickname(userBlock.getBlocked().getNickname())
                .blockedAt(userBlock.getCreatedAt())
                .build();
    }
}