package com.example.trace.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.trace.mission.mission.MissionEntity;

public interface MissionRepository extends JpaRepository<MissionEntity, Long> {
}
