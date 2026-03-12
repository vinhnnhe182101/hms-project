package com.product.hms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomTypeCreateDTO {
    private String name;

    @NotNull @PositiveOrZero
    private BigDecimal basePrice;

    @Min(1)
    private Integer standardCapacity;

    @Min(1)
    private Integer maxCapacity;

    @PositiveOrZero
    private BigDecimal extraPersonFee = BigDecimal.ZERO;

    private String description;
}