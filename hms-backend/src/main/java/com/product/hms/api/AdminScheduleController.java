package com.product.hms.api;

import com.product.hms.dto.request.AssignScheduleRequest;
import com.product.hms.entity.WorkScheduleEntity;
import com.product.hms.service.WorkScheduleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/schedules")
public class AdminScheduleController {

    private final WorkScheduleService workScheduleService;

    public AdminScheduleController(WorkScheduleService workScheduleService) {
        this.workScheduleService = workScheduleService;
    }

    @PostMapping("/assign")
    public ResponseEntity<List<WorkScheduleEntity>> assignSchedule(
            @Valid @RequestBody AssignScheduleRequest request) {
        List<WorkScheduleEntity> schedules = workScheduleService.assignSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(schedules);
    }

    @GetMapping
    public ResponseEntity<List<WorkScheduleEntity>> getSchedules(
            @RequestParam(value = "staffId", required = true) Long staffId,
            @RequestParam(value = "startDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<WorkScheduleEntity> schedules = workScheduleService.getSchedulesByStaffAndDateRange(
                staffId, startDate, endDate);
        return ResponseEntity.ok(schedules);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        workScheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
