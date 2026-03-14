package com.product.hms.repository;

import com.product.hms.entity.WorkScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkScheduleEntity, Long> {

    boolean existsByStaffEntityIdAndShiftEntityIdAndWorkDate(Long staffId, Long shiftId, LocalDate workDate);

    @Query("SELECT ws FROM WorkScheduleEntity ws WHERE ws.staffEntity.id = :staffId " +
            "AND ws.workDate BETWEEN :startDate AND :endDate ORDER BY ws.workDate ASC")
    List<WorkScheduleEntity> findSchedulesByStaffAndDateRange(
            @Param("staffId") Long staffId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT ws FROM WorkScheduleEntity ws WHERE ws.workDate BETWEEN :startDate AND :endDate ORDER BY ws.workDate ASC")
    List<WorkScheduleEntity> findSchedulesByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find all active work schedules for a staff member on a specific date.
     * Used for overnight shift checking.
     */
    @Query("SELECT ws FROM WorkScheduleEntity ws WHERE ws.staffEntity.id = :staffId " +
            "AND ws.workDate = :workDate AND ws.isActive = true")
    List<WorkScheduleEntity> findActiveSchedulesByStaffAndDate(
            @Param("staffId") Long staffId,
            @Param("workDate") LocalDate workDate);
}

