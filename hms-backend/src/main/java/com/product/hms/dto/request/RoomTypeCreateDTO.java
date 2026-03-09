package com.product.hms.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomTypeCreateDTO {
    @NotBlank
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