package com.product.hms.dto.response;

import com.product.hms.enums.RoomStatus;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Getter
@Setter
public class HousekeepingTaskResponses {
    private Long id;
    private String roomNumber;
    private String taskType;
    private String status;
    private Timestamp assignedAt;
    private Timestamp completedAt;
    private RoomStatus roomStatus;  // Đổi thành RoomStatus enum
    private String assigneeName;

    public String getTaskTypeDisplay() {
        switch(taskType) {
            case "CLEANING": return "Cleaning";
            case "INSPECTION": return "Inspection";
            case "MAINTENANCE_SUPPORT": return "Maintenance";
            default: return taskType;
        }
    }

    public String getStatusDisplay() {
        switch(status) {
            case "SCHEDULED": return "Scheduled";
            case "IN_PROGRESS": return "In Progress";
            case "COMPLETED": return "Completed";
            case "CANCELLED": return "Cancelled";
            default: return status;
        }
    }

    public String getStatusColor() {
        switch(status) {
            case "SCHEDULED": return "gray";
            case "IN_PROGRESS": return "yellow";
            case "COMPLETED": return "green";
            case "CANCELLED": return "red";
            default: return "gray";
        }
    }

    // Get room status as string for display
    public String getRoomStatusDisplay() {
        if (roomStatus == null) return "";
        switch(roomStatus) {
            case AVAILABLE: return "Available";
            case OCCUPIED: return "Occupied";
            case DIRTY: return "Dirty";
            case CLEAN: return "Clean";
            case MAINTENANCE: return "Maintenance";
            case RESERVED: return "Reserved";
            default: return roomStatus.name();
        }
    }

    // Get room status color for frontend
    public String getRoomStatusColor() {
        if (roomStatus == null) return "gray";
        switch(roomStatus) {
            case AVAILABLE: return "green";
            case OCCUPIED: return "blue";
            case DIRTY: return "red";
            case CLEAN: return "teal";
            case MAINTENANCE: return "orange";
            case RESERVED: return "violet";
            default: return "gray";
        }
    }

    public String getFormattedAssignedAt() {
        if (assignedAt == null) return "";
        return new SimpleDateFormat("HH:mm dd/MM/yyyy").format(assignedAt);
    }

    public String getFormattedCompletedAt() {
        if (completedAt == null) return "";
        return new SimpleDateFormat("HH:mm dd/MM/yyyy").format(completedAt);
    }
}