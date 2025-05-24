package com.example.trace.mission.controller;

import com.example.trace.mission.mission.DailyMission;
import com.example.trace.mission.service.DailyMissionService;
import com.example.trace.mission.dto.DailyMissionResponse;
import com.example.trace.auth.dto.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
@Slf4j
public class DailyMissionController {

    private final DailyMissionService missionService;

    /**
     * 오늘 할당된 미션을 사용자에게 반환합니다.
     */
    @GetMapping("/today")
    public ResponseEntity<DailyMissionResponse> getTodayMission(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        try {
            String providerId = principalDetails.getUser().getProviderId();
            Optional<DailyMission> dailyMissionOptional = missionService.getTodaysMissionByProviderId(providerId);
            
            if (dailyMissionOptional.isPresent()) {
                int remainingChanges = missionService.getRemainingChanges(providerId);
                return ResponseEntity.ok(DailyMissionResponse.fromEntity(dailyMissionOptional.get(), remainingChanges));
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            log.error("미션 조회 오류", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 일일 미션을 변경합니다. 하루 최대 10번까지 변경 가능합니다.
     */
    @PostMapping("/change")
    public ResponseEntity<DailyMissionResponse> changeDailyMission(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        try {
            String providerId = principalDetails.getUser().getProviderId();
            DailyMission changedMission = missionService.changeDailyMission(providerId);
            int remainingChanges = missionService.getRemainingChanges(providerId);
            
            return ResponseEntity.ok(DailyMissionResponse.fromEntity(changedMission, remainingChanges));
        } catch (Exception e) {
            log.error("미션 변경 오류", e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}
