package com.product.hms.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Getter
@Setter
public class DamageReportResponse {
    private Long id;
    private String roomNumber;
    private String reportedBy;
    private String description;
    private Integer quantity;
    private Double penaltyAmount;
    private String status;
    private Timestamp createdAt;
    private String assetName;
    private String reservationCode;
    
    public String getStatusDisplay() {
        switch(status) {
            case "OPEN": return "Open";
            case "RESOLVED": return "Resolved";
            case "CANCELLED": return "Cancelled";
            default: return status;
        }
    }
    
    public String getStatusColor() {
        switch(status) {
            case "OPEN": return "yellow";
            case "RESOLVED": return "green";
            case "CANCELLED": return "gray";
            default: return "gray";
        }
    }
    
    public String getFormattedCreatedAt() {
        if (createdAt == null) return "";
        return new SimpleDateFormat("HH:mm dd/MM/yyyy").format(createdAt);
    }
    
    public String getPenaltyDisplay() {
        return String.format("%,.0f VND", penaltyAmount);
    }
}