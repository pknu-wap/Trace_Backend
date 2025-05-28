package com.example.trace.mission.service;

import com.example.trace.global.errorcode.MissionErrorCode;
import com.example.trace.global.exception.MissionException;
import com.example.trace.mission.dto.DailyMissionResponse;
import com.example.trace.mission.mission.DailyMission;
import com.example.trace.mission.repository.DailyMissionRepository;
import com.example.trace.mission.mission.Mission;
import com.example.trace.mission.repository.MissionRepository;
import com.example.trace.user.User;
import com.example.trace.user.UserService;
import com.example.trace.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DailyMissionService {

    private final MissionRepository missionRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    
    private static final int MAX_CHANGES_PER_DAY = 10;


    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void assignDailyMissionsToAllUsers() {
        try {
            LocalDate today = LocalDate.now();
            List<User> users = userService.getAllUsers();

            for (User user : users) {
                dailyMissionRepository.findByUserAndDate(user, today)
                        .ifPresent(existing -> dailyMissionRepository.delete(existing));

                Mission randomMission = missionRepository.findRandomMission();
                DailyMission dailyMission = DailyMission.builder()
                                                    .user(user)
                                                    .mission(randomMission)
                                                    .date(today)
                                                    .changeCount(0)
                                                    .build();
                dailyMissionRepository.save(dailyMission);
            }
        } catch (Exception e) {
            System.err.println("자동 미션 할당 실패: " + e.getMessage());
        }
    }

    @Transactional
    public DailyMissionResponse assignDailyMissionsToUserForTest(String providerId){

        LocalDate today = LocalDate.now();
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(()->new UsernameNotFoundException("해당 user가 없습니다 : " + providerId));

        dailyMissionRepository.findByUserAndDate(user, today)
                .ifPresent(existingDailyMission -> dailyMissionRepository.delete(existingDailyMission));

        Mission randomMission = missionRepository.findRandomMission();
        DailyMission dailyMission = DailyMission.builder()
                                                .user(user)
                                                .mission(randomMission)
                                                .date(today)
                                                .changeCount(0)
                                                .build();
        return DailyMissionResponse.fromEntity(dailyMissionRepository.save(dailyMission));
    }


    @Transactional
    public DailyMissionResponse changeDailyMission(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new MissionException(MissionErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now();

        DailyMission currentMission = dailyMissionRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new MissionException(MissionErrorCode.DAILYMISSION_NOT_FOUND));

        if (currentMission.getChangeCount() >= MAX_CHANGES_PER_DAY) {
            throw new MissionException(MissionErrorCode.MISSION_CREATION_LIMIT_EXCEEDED);
        }

        Long currentMissionId = currentMission.getMission().getId();

        Mission newMission = null;
        for (int i = 0; i < 5; i++) {
            Mission randomMission = missionRepository.findRandomMission();
            if (!randomMission.getId().equals(currentMissionId)) {
                newMission = randomMission;
                break;
            }
        }

        if (newMission == null) {
            throw new MissionException(MissionErrorCode.RANDOM_MISSION_NOT_FOUND);
        }

        currentMission.changeMission(newMission);
        return DailyMissionResponse.fromEntity(currentMission);
    }
    
    /**
     * providerId로 오늘의 미션을 조회합니다.
     */
    public DailyMissionResponse getTodaysMissionByProviderId(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(()-> new MissionException(MissionErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now();
        DailyMission todayDailyMission = dailyMissionRepository.findByUserAndDate(user,today)
                .orElseThrow(()->new MissionException(MissionErrorCode.DAILYMISSION_NOT_FOUND));

        return DailyMissionResponse.fromEntity(todayDailyMission);
    }
}

