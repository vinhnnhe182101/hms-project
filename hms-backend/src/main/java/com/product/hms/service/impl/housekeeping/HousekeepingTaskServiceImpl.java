// service/impl/housekeeping/HousekeepingTaskServiceImpl.java
package com.product.hms.service.impl.housekeeping;

import com.product.hms.dto.response.TaskResponse;
import com.product.hms.dto.response.TaskCountResponse;
import com.product.hms.entity.HousekeepingTaskEntity;
import com.product.hms.entity.RoomEntity;
import com.product.hms.entity.StaffEntity;
import com.product.hms.enums.RoomStatus;
import com.product.hms.exception.*;
import com.product.hms.repository.HousekeepingTaskRepository;
import com.product.hms.repository.RoomRepository;
import com.product.hms.service.housekeeping.HousekeepingTaskService;
import com.product.hms.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("housekeepingTaskServiceV2")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HousekeepingTaskServiceImpl implements HousekeepingTaskService {
    
    private final HousekeepingTaskRepository taskRepository;
    private final RoomRepository roomRepository;
    private final SecurityUtil securityUtil;

    @Override
    public List<TaskResponse> getMyTasks() {
        StaffEntity currentStaff = securityUtil.getCurrentStaff();
        log.info("Fetching tasks for staff: {}", currentStaff.getFullName());

        return taskRepository.findByAssigneeEntityIdAndIsActiveTrue(currentStaff.getId())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTodayTasks() {
        StaffEntity currentStaff = securityUtil.getCurrentStaff();
        log.info("Fetching today's tasks for staff: {}", currentStaff.getFullName());

        return taskRepository.findTodayTasks(currentStaff.getId())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskResponse startTask(Long taskId) {
        StaffEntity currentStaff = securityUtil.getCurrentStaff();
        log.info("Staff {} starting task {}", currentStaff.getFullName(), taskId);

        HousekeepingTaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.TASK_NOT_FOUND.code() + ": Không tìm thấy công việc với ID: " + taskId
                ));

        // Kiểm tra task có được giao cho staff này không
        if (!task.getAssigneeEntity().getId().equals(currentStaff.getId())) {
            throw new UnauthorizedException("Công việc không được giao cho bạn");
        }

        // Kiểm tra task có thể bắt đầu không
        if (!"SCHEDULED".equals(task.getStatus())) {
            throw new BadRequest("Không thể bắt đầu công việc ở trạng thái: " + task.getStatus());
        }

        // Cập nhật
        task.setStatus("IN_PROGRESS");
        task.setAssignedAt(Timestamp.from(Instant.now()));

        HousekeepingTaskEntity savedTask = taskRepository.save(task);
        log.info("Task {} started successfully", taskId);

        return convertToResponse(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse completeTask(Long taskId) {
        StaffEntity currentStaff = securityUtil.getCurrentStaff();
        log.info("Staff {} completing task {}", currentStaff.getFullName(), taskId);

        HousekeepingTaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.TASK_NOT_FOUND.code() + ": Không tìm thấy công việc với ID: " + taskId
                ));

        // Kiểm tra quyền
        if (!task.getAssigneeEntity().getId().equals(currentStaff.getId())) {
            throw new UnauthorizedException("Công việc không được giao cho bạn");
        }

        // Kiểm tra trạng thái
        if (!"IN_PROGRESS".equals(task.getStatus())) {
            throw new BadRequest("Không thể hoàn thành công việc ở trạng thái: " + task.getStatus());
        }

        // Cập nhật
        task.setStatus("COMPLETED");
        task.setCompletedAt(Timestamp.from(Instant.now()));

        // Nếu là task CLEANING, cập nhật trạng thái phòng thành CLEAN
        if ("CLEANING".equals(task.getTaskType())) {
            RoomEntity room = task.getRoomEntity();
            room.setStatus(RoomStatus.CLEAN);
            roomRepository.save(room);
            log.info("Room {} marked as CLEAN", room.getRoomNumber());
        }

        HousekeepingTaskEntity savedTask = taskRepository.save(task);
        log.info("Task {} completed successfully", taskId);

        return convertToResponse(savedTask);
    }

    @Override
    public TaskCountResponse getTaskCounts() {
        Long staffId = securityUtil.getCurrentStaffId();
        
        long scheduled = taskRepository.countByAssigneeEntityIdAndStatus(staffId, "SCHEDULED");
        long inProgress = taskRepository.countByAssigneeEntityIdAndStatus(staffId, "IN_PROGRESS");
        long completed = taskRepository.countByAssigneeEntityIdAndStatus(staffId, "COMPLETED");

        return TaskCountResponse.of(scheduled, inProgress, completed);
    }

    private TaskResponse convertToResponse(HousekeepingTaskEntity task) {
        return TaskResponse.builder()
                .id(task.getId())
                .roomNumber(task.getRoomEntity().getRoomNumber())
                .taskType(task.getTaskType())
                .status(task.getStatus())
                .assignedAt(task.getAssignedAt())
                .completedAt(task.getCompletedAt())
                .roomStatus(task.getRoomEntity().getStatus())
                .assigneeName(task.getAssigneeEntity() != null ? task.getAssigneeEntity().getFullName() : null)
                .build();
    }
}