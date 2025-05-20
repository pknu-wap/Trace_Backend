package com.example.trace.mission.controller;

import com.example.trace.mission.mission.DailyMission;
import com.example.trace.mission.service.DailyMissionService;
import com.example.trace.mission.dto.DailyMissionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
     * providerId로 사용자를 식별합니다.
     */
    @GetMapping("/today/{providerId}")
    public ResponseEntity<DailyMissionResponse> getTodayMission(@PathVariable String providerId) {
        try {
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
}
