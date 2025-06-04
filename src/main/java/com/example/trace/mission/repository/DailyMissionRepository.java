package com.example.trace.mission.repository;

import com.example.trace.mission.mission.DailyMission;
import com.example.trace.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {

    // 특정 사용자와 날짜의 미션 여부 확인
    Optional<DailyMission> findByUserAndDate(User user, LocalDate date);


    @Query("SELECT dm FROM DailyMission dm " +
           "WHERE dm.user = :user " +
           "AND dm.isVerified = true " +
           "AND (:cursorId IS NULL OR dm.id < :cursorId) " +
           "ORDER BY dm.date DESC " +
           "LIMIT :pageSize")
    List<DailyMission> findByUserWithCursor(
            @Param("user") User user,
            @Param("cursorId") Long cursorId,
            @Param("pageSize") int pageSize
    );
}