package com.example.trace.mission.service;

import com.example.trace.mission.dailymission.DailyMissionEntity;
import com.example.trace.mission.mission.MissionEntity;
import com.example.trace.mission.repository.DailyMissionRepository;
import com.example.trace.mission.repository.MissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class DailyMissionService {

    private final DailyMissionRepository dailyMissionRepository;
    private final MissionRepository missionRepository;
    private final Random random = new Random();

    public DailyMissionService(DailyMissionRepository dailyMissionRepository, MissionRepository missionRepository) {
        this.dailyMissionRepository = dailyMissionRepository;
        this.missionRepository = missionRepository;
    }

    /**
     * 무작위 미션 할당 또는 재할당
     */
    public DailyMission assignRandomMission(String userId) {
        List<Mission> missions = missionRepository.findAll();
        if (missions.isEmpty()) {
            throw new RuntimeException("저장된 미션이 없습니다.");
        }
        Optional<DailyMission> optionalDailyMission = dailyMissionRepository.findByUserId(userId);
        DailyMission dailyMission;
        Mission newMission;
        if (optionalDailyMission.isPresent()) {
            dailyMission = optionalDailyMission.get();
            if (missions.size() > 1) {
                do {
                    int index = random.nextInt(missions.size());
                    newMission = missions.get(index);
                } while (dailyMission.getMission().getId().equals(newMission.getId()));
            } else {
                newMission = missions.get(0);
            }
            dailyMission.setMission(newMission);
        } else {
            int index = random.nextInt(missions.size());
            newMission = missions.get(index);
            dailyMission = new DailyMission(userId, newMission);
        }
        return dailyMissionRepository.save(dailyMission);
    }

    public Optional<DailyMission> getDailyMissionForUser(String userId) {
        return dailyMissionRepository.findByUserId(userId);
    }
}
