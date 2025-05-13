package com.example.trace.mission.controller;

import com.example.trace.mission.mission.DailyMission;
import com.example.trace.mission.service.DailyMissionService;
import com.example.trace.user.User;
import com.example.trace.user.UserService;
import com.example.trace.user.dto.UserDto;
import com.example.trace.mission.dto.DailyMissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
public class DailyMissionController {

    private final DailyMissionService missionService;
    private final UserService userService;

    /**
     * 오늘 할당된 미션을 사용자에게 반환합니다.
     * 스케줄러가 매일 자정에 미션을 할당하므로, 미션이 존재하지 않으면 204 No Content 상태를 반환합니다.
     */
    @GetMapping("/today/{userId}")
    public ResponseEntity<DailyMissionResponse> getTodayMission(@PathVariable Long userId) {
        // userId로 UserDto를 조회합니다.
        UserDto userDto = userService.getUserInfo(userId.toString());
        if (userDto == null) {
            return ResponseEntity.notFound().build();
        }

        // UserDto를 간단하게 User 엔티티로 변환 (필요한 경우 추가 정보 채워넣기)
        User user = new User();
        user.setId(userDto.getId());

        // 오늘 할당된 미션을 검색합니다.
        Optional<DailyMission> dailyMissionOptional = missionService.getTodaysMission(user);
        return dailyMissionOptional
                .map(mission -> ResponseEntity.ok(new DailyMissionResponse().fromEntity(mission)))
                .orElse(ResponseEntity.noContent().build());
    }
}
