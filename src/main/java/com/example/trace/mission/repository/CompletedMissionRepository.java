package com.example.trace.mission.repository;

import com.example.trace.mission.mission.CompletedMission;
import com.example.trace.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompletedMissionRepository extends JpaRepository<CompletedMission, Long> {
    
    @Query("SELECT cm FROM CompletedMission cm " +
           "WHERE cm.user = :user " +
           "AND (:cursorId IS NULL OR cm.id < :cursorId) " +
           "ORDER BY cm.id DESC " +
           "LIMIT :pageSize")
    List<CompletedMission> findByUserWithCursor(
            @Param("user") User user,
            @Param("cursorId") Long cursorId,
            @Param("pageSize") int pageSize
    );
} 