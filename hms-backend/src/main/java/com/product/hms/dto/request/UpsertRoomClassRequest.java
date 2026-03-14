package com.product.hms.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpsertRoomClassRequest(
        @NotBlank(message = "Room type name is required")
        @Size(max = 255, message = "Room type name must be at most 255 characters")
        String name,

        @NotNull(message = "Standard occupancy is required")
        Integer standardCapacity,

        @NotNull(message = "Max occupancy is required")
        Integer maxCapacity,

        @NotNull(message = "Base rate is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Base rate must be greater than or equal to 0")
        BigDecimal basePrice,

        @NotNull(message = "Extra person fee is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Extra person fee must be greater than or equal to 0")
        BigDecimal extraPersonFee
) {
}