// service/impl/housekeeping/ReportsServiceImpl.java
package com.product.hms.service.impl.housekeeping;

import com.product.hms.dto.response.PerformanceReportResponse;
import com.product.hms.entity.HousekeepingTaskEntity;
import com.product.hms.entity.StaffEntity;
import com.product.hms.enums.FolioItemType;
import com.product.hms.repository.DamageReportRepository;
import com.product.hms.repository.FolioItemRepository;
import com.product.hms.repository.HousekeepingTaskRepository;
import com.product.hms.service.housekeeping.ReportsService;
import com.product.hms.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportsServiceImpl implements ReportsService {

    private final HousekeepingTaskRepository taskRepository;
    private final DamageReportRepository damageReportRepository;
    private final FolioItemRepository folioItemRepository;
    private final SecurityUtil securityUtil;

    @Override
    public PerformanceReportResponse getPerformanceReport(LocalDate startDate, LocalDate endDate) {
        StaffEntity currentStaff = securityUtil.getCurrentStaff();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        // Get tasks in period
        List<HousekeepingTaskEntity> tasks = taskRepository
                .findByAssigneeEntityIdAndAssignedAtBetween(
                        currentStaff.getId(), start, end);

        long totalTasks = tasks.size();
        long completedTasks = tasks.stream()
                .filter(t -> "COMPLETED".equals(t.getStatus()))
                .count();
        double completionRate = totalTasks > 0 ?
                (completedTasks * 100.0 / totalTasks) : 0;

        // Calculate average time per task
        double avgTimePerTask = tasks.stream()
                .filter(t -> t.getCompletedAt() != null && t.getAssignedAt() != null)
                .mapToLong(t -> t.getCompletedAt().getTime() - t.getAssignedAt().getTime())
                .average()
                .orElse(0) / (1000 * 60); // Convert to minutes

        // Get minibar revenue from folio items
        BigDecimal minibarRevenue = folioItemRepository
                .getTotalByStaffAndTypeAndPeriod(
                        currentStaff.getId(),
                        FolioItemType.MINIBAR_CHARGE,
                        start,
                        end)
                .orElse(BigDecimal.ZERO);

        // Get damage stats
        long damageReports = damageReportRepository
                .countByReportedByStaffEntityIdAndCreatedAtBetween(
                        currentStaff.getId(), start, end);

        BigDecimal damagePenalty = damageReportRepository
                .getTotalPenaltyByStaffAndPeriod(
                        currentStaff.getId(), start, end).orElse(BigDecimal.ZERO);

        return PerformanceReportResponse.builder()
                .staffName(currentStaff.getFullName())
                .periodStart(startDate)
                .periodEnd(endDate)
                .totalTasks((int) totalTasks)
                .completedTasks((int) completedTasks)
                .completionRate(completionRate)
                .avgTimePerTask(avgTimePerTask)
                .minibarRevenue(minibarRevenue.doubleValue())
                .damageReports((int) damageReports)
                .damagePenalty(damagePenalty)
                .build();
    }
}