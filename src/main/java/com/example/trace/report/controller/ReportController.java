package com.example.trace.report.controller;

import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.global.response.ApiResponse;
import com.example.trace.report.dto.BlockedUserResponse;
import com.example.trace.report.dto.ReportRequest;
import com.example.trace.report.service.ReportService;
import com.example.trace.report.service.UserBlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "신고 API", description = "신고 관련 API")
public class ReportController {

    private final ReportService reportService;
    private final UserBlockService userBlockService;

    @PostMapping(consumes = "application/json")
    @Operation(summary = "신고 생성", description = "사용자가 신고를 생성합니다.")
    public ResponseEntity<ApiResponse> createReport(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody ReportRequest request) {
        reportService.createReport(principalDetails.getUser().getProviderId(), request);
        return ResponseEntity.ok(ApiResponse.success("신고가 성공적으로 접수되었습니다."));
    }


    @PostMapping("/block/{blockedProviderId}")
    @Operation(summary = "사용자 차단", description = "사용자를 차단합니다.")
    public ResponseEntity<Void> blockUser(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable String blockedProviderId) {
        String blockerProviderId = principalDetails.getUser().getProviderId();
        userBlockService.blockUser(blockerProviderId, blockedProviderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/unblock/{blockedProviderId}")
    @Operation(summary = "사용자 차단 해제", description = "차단된 사용자의 차단을 해제합니다.")
    public ResponseEntity<Void> unblockUser(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable String blockedProviderId) {
        String blockerProviderId = principalDetails.getUser().getProviderId();
        userBlockService.unblockUser(blockerProviderId, blockedProviderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/blocked-users")
    @Operation(summary = "차단한 사용자 목록 조회", description = "현재 사용자가 차단한 모든 사용자 목록을 조회합니다.")
    public ResponseEntity<List<BlockedUserResponse>> getBlockedUsers(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        String blockerProviderId = principalDetails.getUser().getProviderId();
        List<BlockedUserResponse> blockedUsers = userBlockService.getBlockedUsers(blockerProviderId);

        return ResponseEntity.ok(blockedUsers);
    }
}
