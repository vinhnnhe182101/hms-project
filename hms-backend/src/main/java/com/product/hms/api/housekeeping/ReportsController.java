// controller/housekeeping/ReportsController.java
package com.product.hms.api.housekeeping;

import com.product.hms.dto.response.ApiResponse;
import com.product.hms.dto.response.PerformanceReportResponse;
import com.product.hms.service.housekeeping.ReportsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/housekeeping/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HOUSEKEEPING')")
public class ReportsController {
    
    private final ReportsService reportsService;

    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<PerformanceReportResponse>> getPerformanceReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        PerformanceReportResponse report = reportsService.getPerformanceReport(startDate, endDate);
        return ResponseEntity.ok(
            ApiResponse.success("Performance report retrieved successfully", report)
        );
    }
}