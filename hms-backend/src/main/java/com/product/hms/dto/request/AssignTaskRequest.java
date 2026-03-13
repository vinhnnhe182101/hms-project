package com.product.hms.dto.request;

import com.product.hms.enums.HousekeepingTaskType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for assigning a housekeeping task to a staff member.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignTaskRequest {

    @NotNull(message = "Room ID cannot be null")
    private Long roomId;

    @NotNull(message = "Staff ID cannot be null")
    private Long staffId;

    @NotNull(message = "Task type cannot be null")
    private HousekeepingTaskType taskType;
}
