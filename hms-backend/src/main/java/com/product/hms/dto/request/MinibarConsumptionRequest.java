// dto/request/housekeeping/MinibarConsumptionRequest.java
package com.product.hms.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.util.List;

@Data
public class MinibarConsumptionRequest {
    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    @NotNull(message = "Items list is required")
    private List<MinibarItem> items;

    @Data
    public static class MinibarItem {
        @NotNull(message = "Room asset ID is required")
        private Long roomAssetId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
}