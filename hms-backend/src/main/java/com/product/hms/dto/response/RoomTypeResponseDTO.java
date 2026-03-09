
package com.product.hms.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomTypeResponseDTO {
    private Long id;
    private String name;
    private BigDecimal basePrice;
    private Integer standardCapacity;
    private Integer maxCapacity;
    private BigDecimal extraPersonFee;
    private String description;
    private boolean active;
    private int roomCount;
}