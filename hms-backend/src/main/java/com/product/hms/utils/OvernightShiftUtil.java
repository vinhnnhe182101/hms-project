package com.product.hms.utils;

import com.product.hms.entity.ShiftEntity;
import com.product.hms.entity.StaffEntity;
import com.product.hms.service.WorkScheduleService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Utility class for handling overnight shifts (Ca qua đêm) in the HMS.
 *
 * An overnight shift is identified when startTime > endTime (e.g., 22:00 to 08:00 next day).
 * This utility provides helper methods for checking active staff and shift status.
 */
@Component
public class OvernightShiftUtil {

    // Không còn inject WorkScheduleService ở đây nữa

    public boolean isOvernightShift(ShiftEntity shift) {
        if (shift == null || shift.getStartTime() == null || shift.getEndTime() == null) {
            return false;
        }
        return shift.getStartTime().isAfter(shift.getEndTime());
    }

    public String getShiftDescription(ShiftEntity shift) {
        if (shift == null || shift.getStartTime() == null || shift.getEndTime() == null) {
            return "Unknown Shift";
        }
        String description = String.format("%s - %s", shift.getStartTime(), shift.getEndTime());
        if (isOvernightShift(shift)) {
            description += " (Overnight)";
        }
        return description;
    }

    public boolean isTimeWithinShift(ShiftEntity shift, LocalTime timeToCheck) {
        if (shift == null || shift.getStartTime() == null || shift.getEndTime() == null || timeToCheck == null) {
            return false;
        }

        LocalTime startTime = shift.getStartTime();
        LocalTime endTime = shift.getEndTime();

        // Ca thường: startTime < endTime
        if (startTime.isBefore(endTime)) {
            return !timeToCheck.isBefore(startTime) && !timeToCheck.isAfter(endTime);
        }
        // Ca qua đêm: startTime > endTime
        else if (startTime.isAfter(endTime)) {
            return !timeToCheck.isBefore(startTime) || !timeToCheck.isAfter(endTime);
        }
        // Ca 24h: startTime == endTime
        return true;
    }
}
