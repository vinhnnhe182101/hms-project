package com.product.hms.service.housekeeping;

import com.product.hms.dto.response.ScheduleResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ScheduleService {
    List<ScheduleResponse> getMySchedule(LocalDate startDate, LocalDate endDate);
    List<ScheduleResponse> getTodaySchedule();
    Map<String, Object> getScheduleSummary();
}