package com.product.hms.dto.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class TaskStatusUpdateRequest {
    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotNull(message = "Status is required")
    private String status;
}