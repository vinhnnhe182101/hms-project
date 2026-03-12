package com.product.hms.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomAssetResponse {
    private Long id;
    private String roomNumber;
    private String assetName;
    private String categoryName;
    private Integer currentQuantity;
    private Integer initialQuantity;
    private Double price;
    private String status;
    
    public String getStatusDisplay() {
        switch(status) {
            case "Good": return "Good";
            case "Damaged": return "Damaged";
            default: return status;
        }
    }
    
    public String getPriceDisplay() {
        return String.format("%,.0f VND", price);
    }
    
    public Integer getConsumedQuantity() {
        if (initialQuantity == null) return 0;
        return initialQuantity - currentQuantity;
    }
}