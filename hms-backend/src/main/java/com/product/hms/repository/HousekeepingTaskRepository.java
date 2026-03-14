package com.product.hms.repository;

import com.product.hms.entity.HousekeepingTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for HousekeepingTaskEntity
 */
@Repository
public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTaskEntity, Long> {

    List<HousekeepingTaskEntity> findByIsActiveTrue();

    // Find tasks by assignee
    List<HousekeepingTaskEntity> findByAssigneeEntityIdAndIsActiveTrue(Long assigneeId);

    // Find tasks by assignee and status
    List<HousekeepingTaskEntity> findByAssigneeEntityIdAndStatusAndIsActiveTrue(
            Long assigneeId, String status);

    // Find today's tasks
    @Query("SELECT t FROM HousekeepingTaskEntity t WHERE t.assigneeEntity.id = :assigneeId " +
            "AND DATE(t.assignedAt) = CURRENT_DATE AND t.isActive = true")
    List<HousekeepingTaskEntity> findTodayTasks(@Param("assigneeId") Long assigneeId);

    // Find tasks between dates
    @Query("SELECT t FROM HousekeepingTaskEntity t WHERE t.assigneeEntity.id = :staffId " +
            "AND t.assignedAt BETWEEN :start AND :end AND t.isActive = true")
    List<HousekeepingTaskEntity> findByAssigneeEntityIdAndAssignedAtBetween(
            @Param("staffId") Long staffId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // Count tasks by status
    @Query("SELECT COUNT(t) FROM HousekeepingTaskEntity t WHERE t.assigneeEntity.id = :assigneeId " +
            "AND t.status = :status AND t.isActive = true")
    long countByAssigneeEntityIdAndStatus(
            @Param("assigneeId") Long assigneeId,
            @Param("status") String status);

    // Get task statistics
    @Query("SELECT t.status, COUNT(t) FROM HousekeepingTaskEntity t " +
            "WHERE t.assigneeEntity.id = :staffId AND t.assignedAt BETWEEN :start AND :end " +
            "AND t.isActive = true GROUP BY t.status")
    List<Object[]> getTaskStatsByStaffAndPeriod(
            @Param("staffId") Long staffId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // Find task by id and assignee
    Optional<HousekeepingTaskEntity> findByIdAndAssigneeEntityId(Long id, Long assigneeId);
}
