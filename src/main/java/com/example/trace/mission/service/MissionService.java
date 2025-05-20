package com.example.trace.mission.service;

import com.example.trace.mission.mission.Mission;
import com.example.trace.mission.dto.DailyMissionRequestDto;

public interface MissionService {
    Mission getRandomMission();
    Mission changeDailyMission(DailyMissionRequestDto requestDto);
} 