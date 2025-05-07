package com.example.trace.mission.controller;

import com.example.trace.mission.dto.DailyMissionDto;
import com.example.trace.mission.service.DailyMissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daily-mission")
public class DailyMissionController {

    private final DailyMissionService dailyMissionService;

    public DailyMissionController(DailyMissionService dailyMissionService) {
        this.dailyMissionService = dailyMissionService;
    }

    // GET /daily-mission: 오늘 할당된 미션 조회
    @GetMapping
    public ResponseEntity<DailyMissionDto> getTodayDailyMission() {
        DailyMissionDto dailyMissionDto = dailyMissionService.getTodayDailyMission();
        return ResponseEntity.ok(dailyMissionDto);
    }

    // POST /daily-mission/change: 랜덤 미션으로 오늘의 미션 변경
    @PostMapping("/change")
    public ResponseEntity<DailyMissionDto> changeRandomDailyMission() {
        DailyMissionDto dailyMissionDto = dailyMissionService.changeRandomDailyMission();
        return ResponseEntity.ok(dailyMissionDto);
    }
}
