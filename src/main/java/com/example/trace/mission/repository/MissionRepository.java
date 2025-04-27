package com.example.trace.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.trace.mission.mission.MissionEntity;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
}
