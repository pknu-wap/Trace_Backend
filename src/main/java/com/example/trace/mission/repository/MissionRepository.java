package com.example.trace.mission.repository;

import com.example.trace.mission.mission.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    @Query("SELECT m FROM Mission m ORDER BY function('RAND')") // MySQL 기준 랜덤 정렬
    Mission findRandomMission();
}

