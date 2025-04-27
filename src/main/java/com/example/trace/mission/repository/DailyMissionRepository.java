package com.example.trace.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.trace.mission.dailymission.DailyMissionEntity;

import java.util.Optional;

@Repository
public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {
    Optional<DailyMission> findByUserId(String userId);
}
