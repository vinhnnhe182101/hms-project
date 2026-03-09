package com.product.hms.dto.response;

import com.product.hms.enums.HousekeepingTaskStatus;
import com.product.hms.enums.HousekeepingTaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * DTO for Housekeeping Task response data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HousekeepingTaskResponse {

    private Long id;

    private Long roomId;

    private String roomNumber;

    private Long assigneeId;

    private String assigneeName;

    private HousekeepingTaskType taskType;

    private HousekeepingTaskStatus status;

    private Timestamp assignedAt;

    private Timestamp completedAt;
}
