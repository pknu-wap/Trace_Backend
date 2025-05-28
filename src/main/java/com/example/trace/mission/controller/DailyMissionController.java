package com.example.trace.mission.controller;

import com.example.trace.mission.dto.AssignMissionRequest;
import com.example.trace.mission.service.DailyMissionService;
import com.example.trace.mission.dto.DailyMissionResponse;
import com.example.trace.auth.dto.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "mission", description = "미션 API")
public class DailyMissionController {

    private final DailyMissionService missionService;

    /**
     * 오늘 할당된 미션을 사용자에게 반환합니다.
     */
    @GetMapping("/today")
    public ResponseEntity<DailyMissionResponse> getTodayMission(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        String providerId = principalDetails.getUser().getProviderId();
        DailyMissionResponse response = missionService.getTodaysMissionByProviderId(providerId);
        return ResponseEntity.ok(response);
    }

    /**
     * 일일 미션을 변경합니다. 하루 최대 10번까지 변경 가능합니다.
     */
    @PostMapping("/change")
    public ResponseEntity<DailyMissionResponse> changeDailyMission(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        String providerId = principalDetails.getUser().getProviderId();
        DailyMissionResponse response = missionService.changeDailyMission(providerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assign/test")
    public ResponseEntity<DailyMissionResponse> assignDailyMissionsToUserForTest(@RequestBody AssignMissionRequest request){
        String providerId = request.getProviderId();
        return ResponseEntity.ok(missionService.assignDailyMissionsToUserForTest(providerId));
    }

}
