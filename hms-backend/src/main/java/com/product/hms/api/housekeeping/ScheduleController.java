// controller/housekeeping/ScheduleController.java
package com.product.hms.api.housekeeping;

import com.product.hms.dto.response.ApiResponse;
import com.product.hms.dto.response.ScheduleResponse;
import com.product.hms.service.housekeeping.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/housekeeping/schedule")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('HOUSEKEEPING')")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/my-schedule")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getMySchedule(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        List<ScheduleResponse> schedule = scheduleService.getMySchedule(startDate, endDate);
        return ResponseEntity.ok(
                ApiResponse.success("Schedule retrieved successfully", schedule)
        );
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<ScheduleResponse>> getTodaySchedule() {
        ScheduleResponse today = scheduleService.getTodaySchedule();
        return ResponseEntity.ok(
                ApiResponse.success("Today's schedule retrieved successfully", today)
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getScheduleSummary() {
        Map<String, Object> summary = scheduleService.getScheduleSummary();
        return ResponseEntity.ok(
                ApiResponse.success("Schedule summary retrieved successfully", summary)
        );
    }
}