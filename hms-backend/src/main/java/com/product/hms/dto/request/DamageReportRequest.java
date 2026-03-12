package com.product.hms.dto.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class DamageReportRequest {
    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    private Long assetId;
    private Double penaltyAmount;
}