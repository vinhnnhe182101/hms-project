package com.product.hms.service.impl;

import com.product.hms.dto.request.AssignScheduleRequest;
import com.product.hms.entity.ShiftEntity;
import com.product.hms.entity.StaffEntity;
import com.product.hms.entity.WorkScheduleEntity;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.ShiftRepository;
import com.product.hms.repository.StaffRepository;
import com.product.hms.repository.WorkScheduleRepository;
import com.product.hms.service.WorkScheduleService;
import com.product.hms.utils.OvernightShiftUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final StaffRepository staffRepository;
    private final ShiftRepository shiftRepository;
    private final OvernightShiftUtil overnightShiftUtil;

    public WorkScheduleServiceImpl(WorkScheduleRepository workScheduleRepository,
                                  StaffRepository staffRepository,
                                  ShiftRepository shiftRepository,
                                   OvernightShiftUtil overnightShiftUtil) {
        this.workScheduleRepository = workScheduleRepository;
        this.staffRepository = staffRepository;
        this.shiftRepository = shiftRepository;
        this.overnightShiftUtil = overnightShiftUtil;
    }

    @Override
    @Transactional
    public List<WorkScheduleEntity> assignSchedule(AssignScheduleRequest request) {
        // 1. Kiểm tra logic ngày tháng cơ bản
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        // 2. Lấy dữ liệu Staff và Shift
        StaffEntity staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.STAFF_NOT_FOUND, "Staff not found"));

        ShiftEntity shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHIFT_NOT_FOUND, "Shift not found"));

        // 3. Tối ưu: Lấy toàn bộ lịch hiện có của nhân viên trong khoảng thời gian này (1 lần gọi DB)
        List<WorkScheduleEntity> existingSchedules = workScheduleRepository
                .findSchedulesByStaffAndDateRange(request.getStaffId(), request.getStartDate(), request.getEndDate());

        // Tạo một cấu trúc dữ liệu để kiểm tra trùng nhanh trên RAM
        // Key: "workDate_shiftId" - Giúp xác định nhân viên đã có ca cụ thể này vào ngày đó chưa
        Set<String> existingAssignmentKeys = existingSchedules.stream()
                .map(s -> s.getWorkDate().toString() + "_" + s.getShiftEntity().getId())
                .collect(Collectors.toSet());

        List<WorkScheduleEntity> schedulesToSave = new ArrayList<>();
        LocalDate currentDate = request.getStartDate();

        while (!currentDate.isAfter(request.getEndDate())) {
            // Kiểm tra trùng: Nhân viên + Đúng Ca này + Đúng Ngày này
            String currentKey = currentDate.toString() + "_" + request.getShiftId();

            if (existingAssignmentKeys.contains(currentKey)) {
                throw new IllegalArgumentException("Schedule already exists for Shift ID " + request.getShiftId() + " on date: " + currentDate);
            }

            // Tạo Entity mới
            WorkScheduleEntity schedule = new WorkScheduleEntity();
            schedule.setStaffEntity(staff);
            schedule.setShiftEntity(shift);
            schedule.setWorkDate(currentDate);
            schedule.setStatus("SCHEDULED");
            schedule.setIsActive(true);

            schedulesToSave.add(schedule);
            currentDate = currentDate.plusDays(1);
        }

        // 4. Lưu hàng loạt (Batch Save)
        return workScheduleRepository.saveAll(schedulesToSave);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkScheduleEntity> getSchedulesByStaffAndDateRange(Long staffId, LocalDate startDate, LocalDate endDate) {
        // Validate staff exists
        if (!staffRepository.existsById(staffId)) {
            throw new NotFoundException(ErrorCode.STAFF_NOT_FOUND,
                    "Staff not found with id: " + staffId);
        }

        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        return workScheduleRepository.findSchedulesByStaffAndDateRange(staffId, startDate, endDate);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        WorkScheduleEntity schedule = workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PAGE_NOT_FOUND,
                        "Work schedule not found with id: " + scheduleId));

        workScheduleRepository.delete(schedule);
    }

    /**
     * Check if a staff member is currently on shift at a specific date and time.
     *
     * OVERNIGHT SHIFT HANDLING (Ca qua đêm):
     * - An overnight shift is identified when startTime > endTime (e.g., 22:00 to 08:00 next day)
     * - For today's overnight shifts: staff is working if targetTime >= startTime (just started tonight)
     * - For yesterday's overnight shifts: staff is working if targetTime <= endTime (finishing from last night)
     *
     * @param staff the staff entity to check
     * @param targetTime the target date and time to verify
     * @return true if the staff is currently on shift, false otherwise
     */
    @Override

    @Transactional(readOnly = true)

    public boolean isStaffCurrentlyOnShift(StaffEntity staff, LocalDateTime targetTime) {

        if (staff == null || targetTime == null) return false;



        LocalDate targetDate = targetTime.toLocalDate();

        LocalTime targetTimeOnly = targetTime.toLocalTime();

        // 1. Kiểm tra lịch ngày hôm nay

        List<WorkScheduleEntity> todaySchedules = workScheduleRepository

                .findActiveSchedulesByStaffAndDate(staff.getId(), targetDate);


        for (WorkScheduleEntity schedule : todaySchedules) {

            ShiftEntity shift = schedule.getShiftEntity();

            // Nếu là ca thường, check trong khoảng Start-End

            // Nếu là ca qua đêm của hôm nay, chỉ cần targetTime >= startTime

            if (overnightShiftUtil.isTimeWithinShift(shift, targetTimeOnly)) {

                return true;

            }

        }

        // 2. Kiểm tra lịch ngày hôm qua (đối với ca qua đêm chưa kết thúc)

        LocalDate yesterdayDate = targetDate.minusDays(1);

        List<WorkScheduleEntity> yesterdaySchedules = workScheduleRepository

                .findActiveSchedulesByStaffAndDate(staff.getId(), yesterdayDate);

        for (WorkScheduleEntity schedule : yesterdaySchedules) {

            ShiftEntity shift = schedule.getShiftEntity();

            if (overnightShiftUtil.isOvernightShift(shift)) {

                // Nếu ca hôm qua là ca qua đêm, check xem giờ hiện tại có <= endTime không

                if (!targetTimeOnly.isAfter(shift.getEndTime())) return true;

            }

        }



        return false;

    }
}
