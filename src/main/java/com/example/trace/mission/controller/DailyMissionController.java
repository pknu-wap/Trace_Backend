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
import java.util.HashMap;
import java.util.Map;

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
                return ResponseEntity.ok(DailyMissionResponse.fromEntity(dailyMissionOptional.get()));
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
    public ResponseEntity<?> changeDailyMission(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        try {
            String providerId = principalDetails.getUser().getProviderId();
            DailyMission changedMission = missionService.changeDailyMission(providerId);
            
            // 변경된 미션과 남은 변경 횟수를 함께 반환
            Map<String, Object> response = new HashMap<>();
            response.put("mission", DailyMissionResponse.fromEntity(changedMission));
            response.put("remainingChanges", missionService.getRemainingChanges(providerId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("미션 변경 오류", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 남은 미션 변경 횟수를 조회합니다.
     */
    @GetMapping("/changes/remaining")
    public ResponseEntity<Map<String, Integer>> getRemainingChanges(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        try {
            String providerId = principalDetails.getUser().getProviderId();
            Map<String, Integer> response = new HashMap<>();
            response.put("remainingChanges", missionService.getRemainingChanges(providerId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("남은 변경 횟수 조회 오류", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
