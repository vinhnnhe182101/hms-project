// service/housekeeping/HousekeepingTaskService.java
package com.product.hms.service.housekeeping;

import com.product.hms.dto.response.TaskResponse;
import com.product.hms.dto.response.TaskCountResponse;
import java.util.List;

public interface HousekeepingTaskService {
    List<TaskResponse> getMyTasks();
    List<TaskResponse> getTodayTasks();
    TaskResponse startTask(Long taskId);
    TaskResponse completeTask(Long taskId);
    TaskCountResponse getTaskCounts();
}