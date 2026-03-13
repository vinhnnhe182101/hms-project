package com.product.hms.service;

import com.product.hms.dto.request.*;
import com.product.hms.dto.response.*;

import java.util.List;

/**
 * Service interface for Housekeeping Task management
 */
public interface HousekeepingTaskService {

    /**
     * Get all active staff members who are currently on shift in the Housekeeping department
     */
    List<StaffResponse> getAvailableOnShiftHousekeepers();

    /**
     * Assign a housekeeping task to a staff member
     */
    HousekeepingTaskResponse assignTask(AssignTaskRequest request);

    /**
     * Get all active housekeeping tasks
     */
    List<HousekeepingTaskResponse> getAllTasks();

    /**
     * Get a specific housekeeping task by ID
     */
    HousekeepingTaskResponse getTaskById(Long id);

    /**
     * Update a housekeeping task
     */
    HousekeepingTaskResponse updateTask(Long id, UpdateTaskRequest request);

    /**
     * Soft delete a housekeeping task
     */
    void deleteTask(Long id);

}
