package com.product.hms.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCountResponse {
    private long scheduled;     // Chờ làm
    private long inProgress;    // Đang làm
    private long completed;      // Hoàn thành
    private long total;          // Tổng số
    
    // Tính phần trăm hoàn thành
    public double getCompletionRate() {
        if (total == 0) return 0;
        return Math.round((completed * 100.0 / total) * 10) / 10.0;
    }
    
    // Text hiển thị
    public String getSummaryText() {
        return String.format("Completed %d/%d task (%.1f%%)",
            completed, total, getCompletionRate());
    }
}