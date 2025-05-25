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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyMissionService {

    private final MissionRepository missionRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    
    private static final int MAX_CHANGES_PER_DAY = 10;

    public Mission getRandomMission() {
        return missionRepository.findRandomMission();
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void assignDailyMissionsToAllUsers() {
        try {
            LocalDate today = LocalDate.now();
            List<User> users = userService.getAllUsers();

            for (User user : users) {
                dailyMissionRepository.findByUserAndDate(user, today)
                        .ifPresent(existing -> dailyMissionRepository.delete(existing));

                Mission randomMission = getRandomMission();
                DailyMission dailyMission = new DailyMission(user, randomMission, today);
                dailyMissionRepository.save(dailyMission);
            }
        } catch (Exception e) {
            System.err.println("자동 미션 할당 실패: " + e.getMessage());
        }
    }

    @Transactional
    public DailyMission changeDailyMission(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        LocalDate today = LocalDate.now();
        
        DailyMission currentMission = dailyMissionRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new RuntimeException("오늘의 미션을 찾을 수 없습니다."));
        
        if (currentMission.getChangeCount() >= MAX_CHANGES_PER_DAY) {
            throw new RuntimeException("오늘의 미션 변경 횟수(10회)를 모두 사용했습니다.");
        }
        
        Long currentMissionId = currentMission.getMission().getId();
        
        Mission newMission = null;
        for (int i = 0; i < 5; i++) {
            Mission randomMission = getRandomMission();
            if (!randomMission.getId().equals(currentMissionId)) {
                newMission = randomMission;
                break;
            }
        }
        
        if (newMission == null) {
            throw new RuntimeException("다른 미션을 찾을 수 없습니다. 나중에 다시 시도해주세요.");
        }
        
        int currentChangeCount = currentMission.getChangeCount();
        dailyMissionRepository.delete(currentMission);
        
        DailyMission newDailyMission = new DailyMission(user, newMission, today);
        newDailyMission.incrementChangeCount();
        return dailyMissionRepository.save(newDailyMission);
    }
    
    public int getRemainingChanges(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
                
        Optional<DailyMission> missionOpt = dailyMissionRepository.findByUserAndDate(user, LocalDate.now());
        if (missionOpt.isEmpty()) {
            return MAX_CHANGES_PER_DAY;
        }
        
        return MAX_CHANGES_PER_DAY - missionOpt.get().getChangeCount();
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

