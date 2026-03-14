package com.product.hms.service;

import com.product.hms.dto.request.AssignScheduleRequest;
import com.product.hms.dto.response.WorkScheduleResponse;
import com.product.hms.entity.StaffEntity;
import com.product.hms.entity.WorkScheduleEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface WorkScheduleService {

    List<WorkScheduleResponse> assignSchedule(AssignScheduleRequest request);

    List<WorkScheduleResponse> getSchedulesByStaffAndDateRange(Long staffId, LocalDate startDate, LocalDate endDate);

    void deleteSchedule(Long scheduleId);

    /**
     * Check if a staff member is currently on shift at a specific date and time.
     * Handles both normal shifts and overnight shifts (ca qua đêm).
     *
     * @param staff the staff entity
     * @param targetTime the target date and time to check
     * @return true if the staff is currently on shift, false otherwise
     */
    boolean isStaffCurrentlyOnShift(StaffEntity staff, LocalDateTime targetTime);
}
