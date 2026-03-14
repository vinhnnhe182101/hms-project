// dto/response/housekeeping/DamageReportResponse.java
package com.product.hms.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class DamageReportResponse {
    private Long id;
    private String roomNumber;
    private String description;
    private Integer quantity;
    private Double penaltyAmount;
    private String status;
    private Timestamp createdAt;
    private String reportedBy;

    public String getFormattedPenalty() {
        return String.format("$%.2f", penaltyAmount);
    }

    public String getStatusDisplay() {
        switch(status) {
            case "OPEN": return "Open";
            case "RESOLVED": return "Resolved";
            case "CANCELLED": return "Cancelled";
            default: return status;
        }
    }

    public String getFormattedCreatedAt() {
        if (createdAt == null) return "";
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .format(createdAt.toLocalDateTime());
    }
}