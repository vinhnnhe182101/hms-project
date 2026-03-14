// service/housekeeping/ReportsService.java
package com.product.hms.service.housekeeping;

import com.product.hms.dto.response.PerformanceReportResponse;
import java.time.LocalDate;

public interface ReportsService {
    PerformanceReportResponse getPerformanceReport(LocalDate startDate, LocalDate endDate);
}