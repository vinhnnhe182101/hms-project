package com.product.hms.api;

import com.product.hms.dto.request.AssignTaskRequest;
import com.product.hms.dto.request.UpdateTaskRequest;
import com.product.hms.dto.response.HousekeepingTaskResponse;
import com.product.hms.dto.response.StaffResponse;
import com.product.hms.service.HousekeepingTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Housekeeping Task Management
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/housekeeping")
@RequiredArgsConstructor
public class AdminHousekeepingController {

    private final HousekeepingTaskService housekeepingTaskService;

    /**
     * GET /api/v1/admin/housekeeping/staff/available
     * Get all available on-shift housekeeping staff
     */
    @GetMapping("/staff/available")
    public ResponseEntity<List<StaffResponse>> getAvailableOnShiftHousekeepers() {
        log.info("GET request: Fetching available on-shift housekeeping staff");
        List<StaffResponse> staff = housekeepingTaskService.getAvailableOnShiftHousekeepers();
        return ResponseEntity.ok(staff);
    }

    /**
     * POST /api/v1/admin/housekeeping/tasks
     * Assign a housekeeping task
     */
    @PostMapping("/tasks")
    public ResponseEntity<HousekeepingTaskResponse> assignTask(@Valid @RequestBody AssignTaskRequest request) {
        log.info("POST request: Assigning housekeeping task");
        HousekeepingTaskResponse response = housekeepingTaskService.assignTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/admin/housekeeping/tasks
     * Get all housekeeping tasks
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<HousekeepingTaskResponse>> getAllTasks() {
        log.info("GET request: Fetching all housekeeping tasks");
        List<HousekeepingTaskResponse> tasks = housekeepingTaskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * GET /api/v1/admin/housekeeping/tasks/{id}
     * Get a specific housekeeping task
     */
    @GetMapping("/tasks/{id}")
    public ResponseEntity<HousekeepingTaskResponse> getTaskById(@PathVariable Long id) {
        log.info("GET request: Fetching housekeeping task - id: {}", id);
        HousekeepingTaskResponse task = housekeepingTaskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * PUT /api/v1/admin/housekeeping/tasks/{id}
     * Update a housekeeping task
     */
    @PutMapping("/tasks/{id}")
    public ResponseEntity<HousekeepingTaskResponse> updateTask(
            @PathVariable Long id,
            @RequestBody UpdateTaskRequest request) {
        log.info("PUT request: Updating housekeeping task - id: {}", id);
        HousekeepingTaskResponse response = housekeepingTaskService.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/admin/housekeeping/tasks/{id}
     * Delete (soft delete) a housekeeping task
     */
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("DELETE request: Deleting housekeeping task - id: {}", id);
        housekeepingTaskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
