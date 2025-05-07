package com.example.trace.mission.repository;

import com.example.trace.mission.mission.DailyMissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface DailyMissionRepository extends JpaRepository<DailyMissionEntity, Long> {
    DailyMissionEntity findByDate(LocalDate date);
}
