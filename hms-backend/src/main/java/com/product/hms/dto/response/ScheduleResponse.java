// dto/response/housekeeping/ScheduleResponse.java
package com.product.hms.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class ScheduleResponse {
    private Long id;
    private LocalDate date;
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private Integer totalTasks;
    private Integer completedTasks;

    public String getStatusDisplay() {
        switch(status) {
            case "SCHEDULED": return "Scheduled";
            case "IN_PROGRESS": return "In Progress";
            case "COMPLETED": return "Completed";
            case "ON_LEAVE": return "On Leave";
            default: return status;
        }
    }
}