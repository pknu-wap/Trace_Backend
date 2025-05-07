package com.example.trace.mission.service;

import com.example.trace.mission.dto.DailyMissionDto;
import com.example.trace.mission.dto.MissionDto;
import com.example.trace.mission.mission.DailyMissionEntity;
import com.example.trace.mission.mission.MissionEntity;
import com.example.trace.mission.repository.DailyMissionRepository;
import com.example.trace.mission.repository.MissionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class DailyMissionService {

    private final MissionRepository missionRepository;
    private final DailyMissionRepository dailyMissionRepository;

    public DailyMissionService(MissionRepository missionRepository,
                               DailyMissionRepository dailyMissionRepository) {
        this.missionRepository = missionRepository;
        this.dailyMissionRepository = dailyMissionRepository;
    }

    // 오늘 할당된 미션을 조회 (없으면 새로 생성)
    public DailyMissionDto getTodayDailyMission() {
        LocalDate today = LocalDate.now();
        DailyMissionEntity dailyMission = dailyMissionRepository.findByDate(today);
        if (dailyMission == null) {
            dailyMission = createNewDailyMission(today);
        }
        return convertToDto(dailyMission);
    }

    // 랜덤 미션으로 오늘 미션 변경
    public DailyMissionDto changeRandomDailyMission() {
        LocalDate today = LocalDate.now();
        List<MissionEntity> missions = missionRepository.findAll();
        if (missions.isEmpty()) {
            throw new IllegalStateException("미션 데이터가 없습니다.");
        }
        MissionEntity randomMission = missions.get(new Random().nextInt(missions.size()));
        DailyMissionEntity dailyMission = dailyMissionRepository.findByDate(today);
        if (dailyMission == null) {
            dailyMission = new DailyMissionEntity();
            dailyMission.setDate(today);
        }
        dailyMission.setMission(randomMission);
        dailyMissionRepository.save(dailyMission);
        return convertToDto(dailyMission);
    }

    // 오늘 날짜로 새로운 일일 미션 생성
    private DailyMissionEntity createNewDailyMission(LocalDate date) {
        List<MissionEntity> missions = missionRepository.findAll();
        if (missions.isEmpty()) {
            throw new IllegalStateException("미션 데이터가 없습니다.");
        }
        MissionEntity randomMission = missions.get(new Random().nextInt(missions.size()));
        DailyMissionEntity dailyMission = new DailyMissionEntity(randomMission, date);
        dailyMissionRepository.save(dailyMission);
        return dailyMission;
    }

    // 엔티티를 DTO로 변환하는 메서드
    private DailyMissionDto convertToDto(DailyMissionEntity entity) {
        DailyMissionDto dto = new DailyMissionDto();
        dto.setId(entity.getId());
        dto.setDate(entity.getDate());

        MissionEntity missionEntity = entity.getMission();
        MissionDto missionDto = new MissionDto();
        if (missionEntity != null) {
            missionDto.setId(missionEntity.getId());
            missionDto.setTitle(missionEntity.getTitle());
        }
        dto.setMission(missionDto);
        return dto;
    }
}
