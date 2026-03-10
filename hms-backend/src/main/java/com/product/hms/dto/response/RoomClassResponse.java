package com.product.hms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomClassResponse {
    private Long id;
    private String name;
    private BigDecimal basePrice;
    private Integer standardCapacity;
    private Integer maxCapacity;
    private BigDecimal extraPersonFee;
    private RoomImgResponse primaryImage;
    private Long totalRooms;
    private Double averageRating;
}
