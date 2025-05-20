package com.example.trace.mission.service;

import com.example.trace.mission.mission.DailyMission;
import com.example.trace.mission.repository.DailyMissionRepository;
import com.example.trace.mission.mission.Mission;
import com.example.trace.mission.repository.MissionRepository;
import com.example.trace.user.User;
import com.example.trace.user.UserService;
import com.example.trace.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailyMissionService {

    private final MissionRepository missionRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void assignDailyMissionsToAllUsers() {
        try {
            LocalDate today = LocalDate.now();
            List<User> users = userService.getAllUsers();

            for (User user : users) {
                // 오늘 미션이 이미 있다면 삭제하고 새로 할당
                dailyMissionRepository.findByUserAndDate(user, today)
                        .ifPresent(existing -> dailyMissionRepository.delete(existing));

                Mission randomMission = missionRepository.findRandomMission();

                // 빌더 대신 생성자를 직접 호출하여 DailyMission 객체 생성
                DailyMission dailyMission = new DailyMission(user, randomMission, today);

                dailyMissionRepository.save(dailyMission);
            }
        } catch (Exception e) {
            System.err.println("자동 미션 할당 실패: " + e.getMessage());
        }
    }

    public Optional<DailyMission> getTodaysMission(User user) {
        return dailyMissionRepository.findByUserAndDate(user, LocalDate.now());
    }
    
    /**
     * providerId로 오늘의 미션을 조회합니다.
     */
    public Optional<DailyMission> getTodaysMissionByProviderId(String providerId) {
        try {
            if (providerId == null) {
                return Optional.empty();
            }
            
            // providerId로 사용자 조회
            User user = userRepository.findByProviderId(providerId).orElse(null);
            if (user == null) {
                return Optional.empty();
            }
            
            return getTodaysMission(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

