package com.product.hms.dto.request;

import com.product.hms.enums.ServiceCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateServiceRequest(
        @NotBlank(message = "Service name is required")
        @Size(max = 255, message = "Service name must be at most 255 characters")
        String name,

        @NotNull(message = "Service category is required")
        ServiceCategory serviceCategory,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Price must be greater than or equal to 0")
        BigDecimal price
) {
}
