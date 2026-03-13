package com.product.hms.service.impl;

import com.product.hms.dto.request.AssignTaskRequest;
import com.product.hms.dto.request.UpdateTaskRequest;
import com.product.hms.dto.response.HousekeepingTaskResponse;
import com.product.hms.dto.response.StaffResponse;
import com.product.hms.entity.HousekeepingTaskEntity;
import com.product.hms.entity.RoomEntity;
import com.product.hms.entity.StaffEntity;
import com.product.hms.enums.Department;
import com.product.hms.enums.HousekeepingTaskStatus;
import com.product.hms.enums.HousekeepingTaskType;
import com.product.hms.enums.RoomStatus;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.HousekeepingTaskRepository;
import com.product.hms.repository.RoomRepository;
import com.product.hms.repository.StaffRepository;
import com.product.hms.service.HousekeepingTaskService;
import com.product.hms.service.WorkScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of HousekeepingTaskService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class HousekeepingTaskServiceImpl implements HousekeepingTaskService {

    private final HousekeepingTaskRepository housekeepingTaskRepository;
    private final RoomRepository roomRepository;
    private final StaffRepository staffRepository;
    private final WorkScheduleService workScheduleService;

    @Override
    public List<StaffResponse> getAvailableOnShiftHousekeepers() {
        log.info("Fetching available on-shift housekeeping staff");

        List<StaffEntity> housekeepingStaff = staffRepository.findByDepartmentAndIsActiveTrue(Department.HOUSEKEEPING);

        List<StaffResponse> availableStaff = housekeepingStaff.stream()
                .filter(staff -> workScheduleService.isStaffCurrentlyOnShift(staff, LocalDateTime.now()))
                .map(this::mapToStaffResponse)
                .collect(Collectors.toList());

        log.info("Found {} available on-shift housekeeping staff", availableStaff.size());
        return availableStaff;
    }

    @Override
    public HousekeepingTaskResponse assignTask(AssignTaskRequest request) {
        log.info("Assigning housekeeping task - roomId: {}, staffId: {}, taskType: {}",
                request.getRoomId(), request.getStaffId(), request.getTaskType());

        // Find and validate room
        RoomEntity room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ROOM_NOT_FOUND,
                        "Room not found with ID: " + request.getRoomId()));

        if (room.getStatus() != RoomStatus.DIRTY) {
            throw new IllegalStateException("Task can only be assigned to a DIRTY room. Current status: " + room.getStatus());
        }

        // Find and validate staff
        StaffEntity staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.STAFF_NOT_FOUND,
                        "Staff not found with ID: " + request.getStaffId()));

        if (staff.getDepartment() != Department.HOUSEKEEPING) {
            throw new IllegalArgumentException("Staff must be from HOUSEKEEPING department. Current department: " + staff.getDepartment());
        }

        // Validate shift
        if (!workScheduleService.isStaffCurrentlyOnShift(staff, LocalDateTime.now())) {
            throw new IllegalStateException("Staff is not currently on shift");
        }

        // Create and save task
        HousekeepingTaskEntity task = new HousekeepingTaskEntity();
        task.setRoomEntity(room);
        task.setAssigneeEntity(staff);
        task.setTaskType(request.getTaskType().name());
        task.setStatus(HousekeepingTaskStatus.ASSIGNED.name());
        task.setAssignedAt(new Timestamp(System.currentTimeMillis()));
        task.setIsActive(true);

        HousekeepingTaskEntity savedTask = housekeepingTaskRepository.save(task);

        log.info("Task assigned successfully - taskId: {}", savedTask.getId());
        return mapToTaskResponse(savedTask);
    }

    @Override
    public List<HousekeepingTaskResponse> getAllTasks() {
        log.info("Fetching all active housekeeping tasks");

        List<HousekeepingTaskEntity> tasks = housekeepingTaskRepository.findByIsActiveTrue();

        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HousekeepingTaskResponse getTaskById(Long id) {
        log.info("Fetching housekeeping task - id: {}", id);

        HousekeepingTaskEntity task = housekeepingTaskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TASK_NOT_FOUND,
                        "Housekeeping task not found with ID: " + id));

        if (!task.getIsActive()) {
            throw new NotFoundException(ErrorCode.TASK_NOT_FOUND,
                    "Housekeeping task not found with ID: " + id);
        }

        return mapToTaskResponse(task);
    }

    @Override
    public HousekeepingTaskResponse updateTask(Long id, UpdateTaskRequest request) {
        log.info("Updating housekeeping task - id: {}", id);

        HousekeepingTaskEntity task = housekeepingTaskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TASK_NOT_FOUND,
                        "Housekeeping task not found with ID: " + id));

        if (!task.getIsActive()) {
            throw new NotFoundException(ErrorCode.TASK_NOT_FOUND,
                    "Housekeeping task not found with ID: " + id);
        }

        // Update task type if provided
        if (request.getTaskType() != null) {
            task.setTaskType(request.getTaskType().name());
        }

        // Update status if provided
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus().name());

            // Set completed timestamp if status is COMPLETED
            if (request.getStatus() == HousekeepingTaskStatus.COMPLETED) {
                task.setCompletedAt(new Timestamp(System.currentTimeMillis()));
            }
        }

        // Update assignee if provided and different from current
        if (request.getStaffId() != null) {
            if (!request.getStaffId().equals(task.getAssigneeEntity() != null ? task.getAssigneeEntity().getId() : null)) {
                // Validate new staff
                StaffEntity newStaff = staffRepository.findById(request.getStaffId())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.STAFF_NOT_FOUND,
                                "Staff not found with ID: " + request.getStaffId()));

                if (newStaff.getDepartment() != Department.HOUSEKEEPING) {
                    throw new IllegalArgumentException("Staff must be from HOUSEKEEPING department");
                }

                if (!workScheduleService.isStaffCurrentlyOnShift(newStaff, LocalDateTime.now())) {
                    throw new IllegalStateException("New assignee is not currently on shift");
                }

                task.setAssigneeEntity(newStaff);
            }
        }

        HousekeepingTaskEntity updatedTask = housekeepingTaskRepository.save(task);

        log.info("Task updated successfully - taskId: {}", updatedTask.getId());
        return mapToTaskResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {
        log.info("Deleting housekeeping task - id: {}", id);

        HousekeepingTaskEntity task = housekeepingTaskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TASK_NOT_FOUND,
                        "Housekeeping task not found with ID: " + id));

        // Soft delete
        task.setIsActive(false);
        housekeepingTaskRepository.save(task);

        log.info("Task soft deleted - taskId: {}", id);
    }

    /**
     * Map StaffEntity to StaffResponse
     */
    private StaffResponse mapToStaffResponse(StaffEntity staff) {
        return StaffResponse.builder()
                .id(staff.getId())
                .fullName(staff.getFullName())
                .department(staff.getDepartment())
                .status(staff.getStatus())
                .build();
    }

    /**
     * Map HousekeepingTaskEntity to HousekeepingTaskResponse
     */
    private HousekeepingTaskResponse mapToTaskResponse(HousekeepingTaskEntity task) {
        return HousekeepingTaskResponse.builder()
                .id(task.getId())
                .roomId(task.getRoomEntity().getId())
                .roomNumber(task.getRoomEntity().getRoomNumber())
                .assigneeId(task.getAssigneeEntity() != null ? task.getAssigneeEntity().getId() : null)
                .assigneeName(task.getAssigneeEntity() != null ? task.getAssigneeEntity().getFullName() : null)
                .taskType(HousekeepingTaskType.valueOf(task.getTaskType()))
                .status(HousekeepingTaskStatus.valueOf(task.getStatus()))
                .assignedAt(task.getAssignedAt())
                .completedAt(task.getCompletedAt())
                .build();
    }
}