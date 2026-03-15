package com.product.hms.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpsertRoomTypeRequest(
        @NotBlank(message = "Type name is required")
        @Size(max = 50, message = "Type name must be at most 50 characters")
        String typeName,

        @NotNull(message = "Standard occupancy is required")
        @Min(value = 1, message = "Standard occupancy must be at least 1")
        Integer standardOccupancy,

        @NotNull(message = "Max occupancy is required")
        @Min(value = 1, message = "Max occupancy must be at least 1")
        Integer maxOccupancy,

        @NotNull(message = "Base rate per night is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Base rate per night must be greater than or equal to 0")
        BigDecimal baseRatePerNight
) {
}
