package com.example.trace.mission.controller

import com.example.trace.mission.dailymission.DailyMissionEntity;
import com.example.trace.mission.service.DailyMissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/daily-missions")
public class DailyMissionController {

    private final DailyMissionService dailyMissionService;

    public DailyMissionController(DailyMissionService dailyMissionService) {
        this.dailyMissionService = dailyMissionService;
    }

    /**
     * 사용자의 현재 할당된 데일리 미션을 조회합니다.
     * 만약 아직 할당되지 않았다면 랜덤 미션을 할당하여 반환합니다.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<DailyMission> getDailyMission(@PathVariable String userId) {
        return dailyMissionService.getDailyMissionForUser(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(dailyMissionService.assignRandomMission(userId)));
    }

    /**
     * 사용자가 현재 할당된 미션이 마음에 들지 않을 경우,
     * 새로운 미션을 랜덤으로 재할당합니다.
     */
    @PostMapping("/{userId}/refresh")
    public ResponseEntity<DailyMission> refreshDailyMission(@PathVariable String userId) {
        DailyMission updatedMission = dailyMissionService.assignRandomMission(userId);
        return ResponseEntity.ok(updatedMission);
    }
}
