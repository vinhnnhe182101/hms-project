// dto/response/housekeeping/PerformanceReportResponse.java
package com.product.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class PerformanceReportResponse {
    private String staffName;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Integer totalTasks;
    private Integer completedTasks;
    private Double completionRate;
    private Double avgTimePerTask;
    private Double minibarRevenue;
    private Integer damageReports;
    private BigDecimal damagePenalty;

    public String getCompletionRateDisplay() {
        return String.format("%.1f%%", completionRate);
    }

    public String getAvgTimeDisplay() {
        return String.format("%.0f min", avgTimePerTask);
    }

    public String getMinibarRevenueDisplay() {
        return String.format("$%.2f", minibarRevenue);
    }

    public String getDamagePenaltyDisplay() {
        return String.format("$%.2f", damagePenalty);
    }
}