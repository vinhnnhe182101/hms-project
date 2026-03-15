// service/impl/housekeeping/ScheduleServiceImpl.java
package com.product.hms.service.impl.housekeeping;

import com.product.hms.dto.response.ScheduleResponse;
import com.product.hms.entity.ShiftEntity;
import com.product.hms.entity.StaffEntity;
import com.product.hms.entity.WorkScheduleEntity;
import com.product.hms.exception.ResourceNotFoundException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.repository.HousekeepingTaskRepository;
import com.product.hms.repository.WorkScheduleRepository;
import com.product.hms.service.housekeeping.ScheduleService;
import com.product.hms.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleServiceImpl implements ScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final HousekeepingTaskRepository taskRepository;
    private final SecurityUtil securityUtil;

    @Override
    public List<ScheduleResponse> getMySchedule(LocalDate startDate, LocalDate endDate) {
        StaffEntity currentStaff = securityUtil.getCurrentStaff();
        log.info("Fetching schedule for staff {} from {} to {}",
                currentStaff.getFullName(), startDate, endDate);

        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : start.plusMonths(1).minusDays(1);

        List<WorkScheduleEntity> schedules = workScheduleRepository
                .findByStaffEntityIdAndWorkDateBetween(
                        currentStaff.getId(), start, end);

        return schedules.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleResponse getTodaySchedule() {
        StaffEntity currentStaff = securityUtil.getCurrentStaff();
        LocalDate today = LocalDate.now();

        WorkScheduleEntity schedule = workScheduleRepository
                .findByStaffEntityIdAndWorkDate(currentStaff.getId(), today)
                .orElse(null);

        if (schedule == null) {
            return ScheduleResponse.builder()
                    .date(today)
                    .shiftName("Day Off")
                    .status("OFF")
                    .totalTasks(0)
                    .completedTasks(0)
                    .build();
        }

        return convertToResponse(schedule);
    }

    @Override
    public Map<String, Object> getScheduleSummary() {
        StaffEntity currentStaff = securityUtil.getCurrentStaff();
        LocalDate startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<WorkScheduleEntity> weekSchedules = workScheduleRepository
                .findByStaffEntityIdAndWorkDateBetween(
                        currentStaff.getId(), startOfWeek, endOfWeek);

        long totalShifts = weekSchedules.size();
        long completedShifts = weekSchedules.stream()
                .filter(s -> "COMPLETED".equals(s.getStatus()))
                .count();

        long totalHours = weekSchedules.stream()
                .filter(s -> s.getShiftEntity() != null)
                .mapToLong(s -> {
                    ShiftEntity shift = s.getShiftEntity();
                    return java.time.Duration.between(shift.getStartTime(), shift.getEndTime()).toHours();
                }).sum();

        // Đếm tổng số tasks trong tuần
        long totalTasks = 0;
        long completedTasks = 0;

        for (WorkScheduleEntity schedule : weekSchedules) {
            totalTasks += countTasksForShift(schedule);
            completedTasks += countCompletedTasksForShift(schedule);
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalShifts", totalShifts);
        summary.put("completedShifts", completedShifts);
        summary.put("upcomingShifts", totalShifts - completedShifts);
        summary.put("totalHours", totalHours);
        summary.put("totalTasks", totalTasks);
        summary.put("completedTasks", completedTasks);
        summary.put("completionRate", totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0);

        return summary;
    }

    private ScheduleResponse convertToResponse(WorkScheduleEntity schedule) {
        if (schedule.getShiftEntity() == null) {
            return ScheduleResponse.builder()
                    .id(schedule.getId())
                    .date(schedule.getWorkDate())
                    .shiftName("Day Off")
                    .status(schedule.getStatus())
                    .totalTasks(0)
                    .completedTasks(0)
                    .build();
        }

        ShiftEntity shift = schedule.getShiftEntity();

        // Đếm số task thực tế cho ngày này
        int totalTasks = countTasksForShift(schedule);
        int completedTasks = countCompletedTasksForShift(schedule);

        return ScheduleResponse.builder()
                .id(schedule.getId())
                .date(schedule.getWorkDate())
                .shiftName(shift.getShiftName())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .status(schedule.getStatus())
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .build();
    }

    /**
     * Đếm tổng số tasks được giao cho nhân viên trong ngày này
     */
    private int countTasksForShift(WorkScheduleEntity schedule) {
        if (schedule == null || schedule.getStaffEntity() == null) return 0;

        LocalDate workDate = schedule.getWorkDate();
        Long staffId = schedule.getStaffEntity().getId();

        // Chuyển đổi LocalDate sang LocalDateTime để query
        LocalDateTime startOfDay = workDate.atStartOfDay();
        LocalDateTime endOfDay = workDate.atTime(LocalTime.MAX);

        // Đếm tasks được giao trong ngày này
        return taskRepository.countByAssigneeEntityIdAndAssignedAtBetween(
                staffId, startOfDay, endOfDay
        );
    }

    /**
     * Đếm số tasks đã hoàn thành trong ngày này
     */
    private int countCompletedTasksForShift(WorkScheduleEntity schedule) {
        if (schedule == null || schedule.getStaffEntity() == null) return 0;

        LocalDate workDate = schedule.getWorkDate();
        Long staffId = schedule.getStaffEntity().getId();

        // Chuyển đổi LocalDate sang LocalDateTime để query
        LocalDateTime startOfDay = workDate.atStartOfDay();
        LocalDateTime endOfDay = workDate.atTime(LocalTime.MAX);

        // Đếm tasks đã hoàn thành (status = 'COMPLETED') trong ngày này
        return taskRepository.countByAssigneeEntityIdAndStatusAndAssignedAtBetween(
                staffId, "COMPLETED", startOfDay, endOfDay
        );
    }
}