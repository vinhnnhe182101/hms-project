package com.product.hms.dto.request;

import com.product.hms.enums.HousekeepingTaskStatus;
import com.product.hms.enums.HousekeepingTaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating a housekeeping task.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {

    private HousekeepingTaskStatus status;

    private HousekeepingTaskType taskType;

    private Long staffId;
}
