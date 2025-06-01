package com.example.trace.mission.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MissionDateUtil {
    private static final int MISSION_START_HOUR = 7;

    public static LocalDate getMissionDate() {
        LocalDateTime now = LocalDateTime.now();
        if (now.getHour() < MISSION_START_HOUR) {
            // 오전 7시 이전이면 전날 미션을 조회
            return now.toLocalDate().minusDays(1);
        }
        return now.toLocalDate();
    }
}
