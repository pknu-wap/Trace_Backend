package com.example.trace.mission.controller;

import com.example.trace.mission.dto.CompletedMissionResponse;
import com.example.trace.mission.service.CompletedMissionService;
import com.example.trace.auth.dto.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/missions/completed")
@RequiredArgsConstructor
@Slf4j
public class CompletedMissionController {

    private final CompletedMissionService completedMissionService;

    /**
     * 사용자의 완료된 미션 목록을 조회합니다.
     * 커서 기반 페이지네이션을 사용합니다.
     */
    @GetMapping
    public ResponseEntity<List<CompletedMissionResponse>> getCompletedMissions(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(required = false) Long cursorId) {
        try {
            String providerId = principalDetails.getUser().getProviderId();
            List<CompletedMissionResponse> completedMissions = 
                completedMissionService.getUserCompletedMissions(providerId, cursorId);
            
            return ResponseEntity.ok(completedMissions);
        } catch (Exception e) {
            log.error("완료된 미션 조회 오류", e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 