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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class DailyMissionService {

    private final MissionRepository missionRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    
    // 사용자별 미션 변경 횟수를 추적하는 맵 (키: providerId, 값: 변경 횟수)
    private final Map<String, Integer> missionChangeCountMap = new ConcurrentHashMap<>();
    
    // 최대 변경 가능 횟수
    private static final int MAX_CHANGES_PER_DAY = 10;

    @Scheduled(cron = "0 * * * * *")
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

    /**
     * 사용자의 남은 미션 변경 횟수를 조회합니다.
     */
    public int getRemainingChanges(String providerId) {
        int usedChanges = missionChangeCountMap.getOrDefault(providerId, 0);
        return Math.max(0, MAX_CHANGES_PER_DAY - usedChanges);
    }
    
    /**
     * 사용자의 일일 미션을 변경합니다.
     * 하루 최대 10번까지 변경 가능하며, 직전 미션과 다른 미션이 할당됩니다.
     */
    @Transactional
    public DailyMission changeDailyMission(String providerId) {
        // 사용자 조회
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        LocalDate today = LocalDate.now();
        
        // 사용자의 오늘 미션 조회
        DailyMission currentMission = dailyMissionRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new RuntimeException("오늘의 미션을 찾을 수 없습니다."));
        
        // 변경 횟수 확인
        int changeCount = missionChangeCountMap.getOrDefault(providerId, 0);
        if (changeCount >= MAX_CHANGES_PER_DAY) {
            throw new RuntimeException("오늘의 미션 변경 횟수(10회)를 모두 사용했습니다.");
        }
        
        // 현재 미션 ID 기억
        Long currentMissionId = currentMission.getMission().getId();
        
        // 새로운 미션 할당 (최대 10번 시도하여 다른 미션 찾기)
        Mission newMission = null;
        for (int i = 0; i < 10; i++) {
            Mission randomMission = missionRepository.findRandomMission();
            if (!randomMission.getId().equals(currentMissionId)) {
                newMission = randomMission;
                break;
            }
        }
        
        // 10번 시도해도 다른 미션을 찾지 못한 경우
        if (newMission == null) {
            throw new RuntimeException("다른 미션을 찾을 수 없습니다. 나중에 다시 시도해주세요.");
        }
        
        // 기존 미션 삭제
        dailyMissionRepository.delete(currentMission);
        
        // 새 미션 생성
        DailyMission newDailyMission = new DailyMission(user, newMission, today);
        dailyMissionRepository.save(newDailyMission);
        
        // 변경 횟수 증가
        missionChangeCountMap.put(providerId, changeCount + 1);
        
        return newDailyMission;
    }
    
    /**
     * 매일 자정에 미션 변경 횟수 초기화
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void resetMissionChangeCount() {
        missionChangeCountMap.clear();
    }
}

