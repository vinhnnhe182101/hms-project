package com.product.hms.api.housekeeping;

import com.product.hms.dto.response.ApiResponse;
import com.product.hms.dto.response.TaskResponse;
import com.product.hms.dto.response.TaskCountResponse;
import com.product.hms.service.housekeeping.HousekeepingTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/housekeeping/tasks")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('HOUSEKEEPING')")
public class HousekeepingTaskController {

    @Qualifier("housekeepingTaskServiceV2")
    private final HousekeepingTaskService taskService;

    @GetMapping("/my-tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getMyTasks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Current authorities: " + auth.getAuthorities());
        List<TaskResponse> tasks = taskService.getMyTasks();
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách công việc thành công", tasks)
        );
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTodayTasks() {
        List<TaskResponse> tasks = taskService.getTodayTasks();
        return ResponseEntity.ok(
                ApiResponse.success("Lấy công việc hôm nay thành công", tasks)
        );
    }

    @PostMapping("/{taskId}/start")
    public ResponseEntity<ApiResponse<TaskResponse>> startTask(@PathVariable Long taskId) {
        TaskResponse task = taskService.startTask(taskId);
        return ResponseEntity.ok(
                ApiResponse.success("Bắt đầu công việc thành công", task)
        );
    }

    @PostMapping("/{taskId}/complete")
    public ResponseEntity<ApiResponse<TaskResponse>> completeTask(@PathVariable Long taskId) {
        TaskResponse task = taskService.completeTask(taskId);
        return ResponseEntity.ok(
                ApiResponse.success("Hoàn thành công việc thành công", task)
        );
    }

    @GetMapping("/counts")
    public ResponseEntity<ApiResponse<TaskCountResponse>> getTaskCounts() {
        TaskCountResponse counts = taskService.getTaskCounts();
        return ResponseEntity.ok(
                ApiResponse.success("Lấy thống kê thành công", counts)
        );
    }
}