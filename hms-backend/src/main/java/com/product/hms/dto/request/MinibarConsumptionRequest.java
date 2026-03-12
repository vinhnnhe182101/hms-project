package com.product.hms.dto.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.util.List;

@Getter
@Setter
public class MinibarConsumptionRequest {
    @NotNull(message = "Room ID is required")
    private Long roomId;
    
    @NotNull(message = "Items are required")
    private List<MinibarItemRequest> items;
    
    @Getter
    @Setter
    public static class MinibarItemRequest {
        @NotNull(message = "Room asset ID is required")
        private Long roomAssetId;
        
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
}