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
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {

    // 특정 사용자와 날짜의 미션 여부 확인
    Optional<DailyMission> findByUserAndDate(User user, LocalDate date);

    // 완료된 미션만 조회 (페이지네이션)
    Page<DailyMission> findByUserAndIsVerifiedTrueOrderByCompletedAtDesc(User user, Pageable pageable);

    // 특정 기간의 미션 조회
    List<DailyMission> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);

    // providerId로 완료된 미션 조회
    @Query("SELECT dm FROM DailyMission dm " +
           "JOIN dm.user u " +
           "WHERE u.providerId = :providerId " +
           "AND dm.isVerified = true " +
           "ORDER BY dm.completedAt DESC")
    List<DailyMission> findCompletedMissionsByProviderId(@Param("providerId") String providerId);

    // providerId로 특정 기간의 미션 조회
    @Query("SELECT dm FROM DailyMission dm " +
           "JOIN dm.user u " +
           "WHERE u.providerId = :providerId " +
           "AND dm.date BETWEEN :startDate AND :endDate " +
           "ORDER BY dm.date DESC")
    List<DailyMission> findMissionsByProviderIdAndDateBetween(
            @Param("providerId") String providerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // providerId로 완료된 미션 조회 (커서 기반 페이지네이션)
    @Query("SELECT dm FROM DailyMission dm " +
           "WHERE dm.user = :user " +
           "AND dm.isVerified = true " +
           "AND (:cursorId IS NULL OR dm.id < :cursorId) " +
           "ORDER BY dm.completedAt DESC " +
           "LIMIT :pageSize")
    List<DailyMission> findByUserWithCursor(
            @Param("user") User user,
            @Param("cursorId") Long cursorId,
            @Param("pageSize") int pageSize
    );
}