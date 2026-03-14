// dto/response/housekeeping/TaskResponse.java
package com.product.hms.dto.response;

import com.product.hms.enums.RoomStatus;
import lombok.Builder;
import lombok.Getter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Getter
@Builder
public class TaskResponse {
    private Long id;
    private String roomNumber;
    private String taskType;
    private String taskTypeDisplay;
    private String status;
    private String statusDisplay;
    private String statusColor;
    private Timestamp assignedAt;
    private Timestamp completedAt;
    private String roomStatus;
    private String roomStatusDisplay;
    private String roomStatusColor;
    private String assigneeName;
    private String formattedAssignedAt;
    private String formattedCompletedAt;

    public static class TaskResponseBuilder {
        public TaskResponseBuilder taskType(String taskType) {
            this.taskType = taskType;
            this.taskTypeDisplay = switch (taskType) {
                case "CLEANING" -> "Cleaning";
                case "INSPECTION" -> "Inspection";
                case "MAINTENANCE_SUPPORT" -> "Maintenance";
                default -> taskType;
            };
            return this;
        }

        public TaskResponseBuilder status(String status) {
            this.status = status;
            this.statusDisplay = switch (status) {
                case "SCHEDULED" -> "Scheduled";
                case "IN_PROGRESS" -> "In Progress";
                case "COMPLETED" -> "Completed";
                case "CANCELLED" -> "Cancelled";
                default -> status;
            };
            this.statusColor = switch (status) {
                case "SCHEDULED" -> "gray";
                case "IN_PROGRESS" -> "yellow";
                case "COMPLETED" -> "green";
                case "CANCELLED" -> "red";
                default -> "gray";
            };
            return this;
        }

        public TaskResponseBuilder roomStatus(RoomStatus roomStatus) {
            if (roomStatus == null) return this;
            this.roomStatus = roomStatus.name();
            this.roomStatusDisplay = switch (roomStatus) {
                case AVAILABLE -> "Available";
                case OCCUPIED -> "Occupied";
                case DIRTY -> "Dirty";
                case CLEAN -> "Clean";
                case MAINTENANCE -> "Maintenance";
                default -> roomStatus.name();
            };
            this.roomStatusColor = switch (roomStatus) {
                case AVAILABLE -> "green";
                case OCCUPIED -> "blue";
                case DIRTY -> "red";
                case CLEAN -> "teal";
                case MAINTENANCE -> "orange";
                default -> "gray";
            };
            return this;
        }

        public TaskResponseBuilder assignedAt(Timestamp assignedAt) {
            this.assignedAt = assignedAt;
            if (assignedAt != null) {
                this.formattedAssignedAt = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(assignedAt);
            }
            return this;
        }

        public TaskResponseBuilder completedAt(Timestamp completedAt) {
            this.completedAt = completedAt;
            if (completedAt != null) {
                this.formattedCompletedAt = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(completedAt);
            }
            return this;
        }
    }
}