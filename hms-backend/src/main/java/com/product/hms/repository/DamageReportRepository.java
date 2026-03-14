package com.product.hms.repository;

import com.product.hms.entity.DamageReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DamageReportRepository extends JpaRepository<DamageReportEntity, Long> {
    // Find reports by staff
    List<DamageReportEntity> findByReportedByStaffEntityId(Long staffId);

    // Find reports by room
    List<DamageReportEntity> findByRoomEntityId(Long roomId);

    // Find reports by reservation
    List<DamageReportEntity> findByReservationEntityId(Long reservationId);

    // Find reports by status
    List<DamageReportEntity> findByStatus(String status);

    // Count reports by staff in period
    @Query("SELECT COUNT(d) FROM DamageReportEntity d WHERE d.reportedByStaffEntity.id = :staffId " +
            "AND d.createdAt BETWEEN :start AND :end")
    long countByReportedByStaffEntityIdAndCreatedAtBetween(
            @Param("staffId") Long staffId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // Get total penalty by staff in period
    @Query("SELECT COALESCE(SUM(d.penaltyAmount), 0) FROM DamageReportEntity d " +
            "WHERE d.reportedByStaffEntity.id = :staffId " +
            "AND d.createdAt BETWEEN :start AND :end")
    Optional<BigDecimal> getTotalPenaltyByStaffAndPeriod(
            @Param("staffId") Long staffId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // Get damage statistics
    @Query("SELECT d.status, COUNT(d), COALESCE(SUM(d.penaltyAmount), 0) FROM DamageReportEntity d " +
            "WHERE d.reportedByStaffEntity.id = :staffId " +
            "AND d.createdAt BETWEEN :start AND :end " +
            "GROUP BY d.status")
    List<Object[]> getDamageStatsByStaffAndPeriod(
            @Param("staffId") Long staffId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
