package com.example.trace.mission.repository;

import com.example.trace.mission.mission.DailyMission;
import com.example.trace.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {

    // 특정 사용자와 날짜의 미션 여부 확인
    Optional<DailyMission> findByUserAndCreatedAt(User user, LocalDate createdAt);


    @Query("SELECT dm FROM DailyMission dm " +
           "WHERE dm.user = :user " +
           "AND dm.isVerified = true " +
           "AND (:cursorDate IS NULL OR dm.createdAt < :cursorDate) " +
           "ORDER BY dm.id DESC " +
           "LIMIT :pageSize")
    List<DailyMission> findVerifiedMissionsWithCursor(
            @Param("user") User user,
            @Param("cursorDate") LocalDate cursorDate,
            @Param("pageSize") int pageSize
    );
}