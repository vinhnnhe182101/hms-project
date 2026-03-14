// dto/request/housekeeping/DamageReportRequest.java
package com.product.hms.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

@Data
public class DamageReportRequest {
    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Penalty amount is required")
    @Min(value = 0, message = "Penalty amount must be positive")
    private Double penaltyAmount;

    private Long assetId;
}