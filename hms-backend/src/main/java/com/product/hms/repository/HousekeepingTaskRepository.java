package com.product.hms.repository;

import com.product.hms.entity.HousekeepingTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for HousekeepingTaskEntity
 */
@Repository
public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTaskEntity, Long> {

    /**
     * Find all active housekeeping tasks
     */
    List<HousekeepingTaskEntity> findByIsActiveTrue();
}
