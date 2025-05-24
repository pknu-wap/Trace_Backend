package com.example.trace.mission.repository;

import com.example.trace.mission.mission.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    @Query(value = "SELECT * FROM mission ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Mission findRandomMission();
}

