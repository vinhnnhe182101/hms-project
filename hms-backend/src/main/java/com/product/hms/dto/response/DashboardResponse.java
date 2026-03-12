package com.product.hms.dto.response;

import com.product.hms.enums.RoomStatus;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DashboardResponse {
    private TaskCountResponse taskCounts;
    private List<HousekeepingTaskResponse> todayTasks;
    private List<RoomStatusResponse> roomStatus;
    private List<DamageReportResponse> recentReports;

    @Getter
    @Setter
    public static class RoomStatusResponse {
        private Long id;
        private String roomNumber;
        private RoomStatus status;  // Đổi thành RoomStatus enum
        private String roomClassName;
        private Boolean hasTaskToday;

        public String getStatusDisplay() {
            if (status == null) return "";
            switch(status) {
                case AVAILABLE: return "Available";
                case OCCUPIED: return "Occupied";
                case DIRTY: return "Dirty";
                case CLEAN: return "Clean";
                case MAINTENANCE: return "Maintenance";
                case RESERVED: return "Reserved";
                default: return status.name();
            }
        }

        public String getStatusColor() {
            if (status == null) return "gray";
            switch(status) {
                case AVAILABLE: return "green";
                case OCCUPIED: return "blue";
                case DIRTY: return "red";
                case CLEAN: return "teal";
                case MAINTENANCE: return "orange";
                case RESERVED: return "violet";
                default: return "gray";
            }
        }
    }
}