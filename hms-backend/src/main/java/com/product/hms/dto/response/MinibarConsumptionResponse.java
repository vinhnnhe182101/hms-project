package com.product.hms.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Getter
@Setter
public class MinibarConsumptionResponse {
    private Long id;
    private String roomNumber;
    private String assetName;
    private String categoryName;
    private Integer quantityConsumed;
    private Double priceAtTime;
    private Double totalPrice;
    private String reportedBy;
    private Timestamp createdAt;
    private Integer remainingQuantity;
    
    public String getFormattedCreatedAt() {
        if (createdAt == null) return "";
        return new SimpleDateFormat("HH:mm dd/MM/yyyy").format(createdAt);
    }
    
    public String getTotalPriceDisplay() {
        return String.format("%,.0f VND", totalPrice);
    }
}