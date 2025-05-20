package com.example.trace.mission.repository;

import com.example.trace.mission.mission.DailyMission;
import com.example.trace.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {

    // 특정 사용자와 날짜의 미션 여부 확인
    Optional<DailyMission> findByUserAndDate(User user, LocalDate date);
}