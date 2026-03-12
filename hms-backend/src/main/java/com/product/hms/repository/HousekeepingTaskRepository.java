package com.product.hms.repository;

import com.product.hms.entity.HousekeepingTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for HousekeepingTaskEntity
 */
@Repository
public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTaskEntity, Long> {

    List<HousekeepingTaskEntity> findByIsActiveTrue();

    List<HousekeepingTaskEntity> findByAssigneeEntityIdAndIsActiveTrue(Long assigneeId);

    List<HousekeepingTaskEntity> findByAssigneeEntityIdAndStatusAndIsActiveTrue(Long assigneeId, String status);

    @Query("SELECT t FROM HousekeepingTaskEntity t WHERE t.assigneeEntity.id = :assigneeId " +
            "AND DATE(t.assignedAt) = CURRENT_DATE AND t.isActive = true")
    List<HousekeepingTaskEntity> findTodayTasks(@Param("assigneeId") Long assigneeId);

    @Query("SELECT COUNT(t) FROM HousekeepingTaskEntity t WHERE t.assigneeEntity.id = :assigneeId " +
            "AND t.status = :status AND t.isActive = true")
    long countByAssigneeEntityIdAndStatus(@Param("assigneeId") Long assigneeId, @Param("status") String status);
}
