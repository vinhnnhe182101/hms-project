package com.product.hms.api.housekeeping;

import com.product.hms.dto.request.TaskStatusUpdateRequest;
import com.product.hms.dto.response.*;
import com.product.hms.service.HousekeepingTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/housekeeping/tasks")
@RequiredArgsConstructor
@Slf4j
public class HousekeepingTaskController {
    
//    private final HousekeepingTaskService taskService;
//
//    @GetMapping("/my-tasks")
//    @PreAuthorize("hasRole('HOUSEKEEPING')")
//    public ResponseEntity<ApiResponse<List<HousekeepingTaskResponses>>> getMyTasks() {
//        log.info("REST request to get my tasks");
//        List<HousekeepingTaskResponses> tasks = taskService.getMyTasks();
//        return ResponseEntity.ok(
//            ApiResponse.success("Get tasks successfully", tasks)
//        );
//    }
//
//    @GetMapping("/today")
//    @PreAuthorize("hasRole('HOUSEKEEPING')")
//    public ResponseEntity<ApiResponse<List<HousekeepingTaskResponses>>> getTodayTasks() {
//        log.info("REST request to get today's tasks");
//        List<HousekeepingTaskResponses> tasks = taskService.getTodayTasks();
//        return ResponseEntity.ok(
//            ApiResponse.success("Get today's tasks successfully", tasks)
//        );
//    }
//
//    @GetMapping("/status/{status}")
//    @PreAuthorize("hasRole('HOUSEKEEPING')")
//    public ResponseEntity<ApiResponse<List<HousekeepingTaskResponses>>> getTasksByStatus(
//            @PathVariable String status) {
//        log.info("REST request to get tasks by status: {}", status);
//        List<HousekeepingTaskResponses> tasks = taskService.getMyTasksByStatus(status);
//        return ResponseEntity.ok(
//            ApiResponse.success("Get tasks by status successfully", tasks)
//        );
//    }
//
//    @GetMapping("/counts")
//    @PreAuthorize("hasRole('HOUSEKEEPING')")
//    public ResponseEntity<ApiResponse<TaskCountResponse>> getTaskCounts() {
//        log.info("REST request to get task counts");
//        TaskCountResponse counts = taskService.getTaskCounts();
//        return ResponseEntity.ok(
//            ApiResponse.success("Get task counts successfully", counts)
//        );
//    }
//    @PostMapping("/{taskId}/start")
//    @PreAuthorize("hasRole('HOUSEKEEPING')")
//    public ResponseEntity<ApiResponse<HousekeepingTaskResponses>> startTask(
//            @PathVariable Long taskId) {
//        log.info("REST request to start task: {}", taskId);
//        HousekeepingTaskResponses response = taskService.startTask(taskId);
//        return ResponseEntity.ok(
//                ApiResponse.success("Task started successfully", response)
//        );
//    }
//
//    @PostMapping("/{taskId}/complete")
//    @PreAuthorize("hasRole('HOUSEKEEPING')")
//    public ResponseEntity<ApiResponse<HousekeepingTaskResponses>> completeTask(
//            @PathVariable Long taskId) {
//        log.info("REST request to complete task: {}", taskId);
//        HousekeepingTaskResponses response = taskService.completeTask(taskId);
//        return ResponseEntity.ok(
//                ApiResponse.success("Task completed successfully", response)
//        );
//    }
//
//    @PutMapping("/status")
//    @PreAuthorize("hasRole('HOUSEKEEPING')")
//    public ResponseEntity<ApiResponse<HousekeepingTaskResponses>> updateTaskStatus(
//            @Valid @RequestBody TaskStatusUpdateRequest request) {
//        log.info("REST request to update task status: {}", request);
//        HousekeepingTaskResponses response = taskService.updateTaskStatus(request);
//        return ResponseEntity.ok(
//                ApiResponse.success("Task status updated successfully", response)
//        );
//    }
}