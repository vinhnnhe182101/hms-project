package com.product.hms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomClassDetailResponse {

    private Long id;
    private String name;

    private Integer standardCapacity;

    private Integer maxCapacity;

    private BigDecimal basePrice;

    private BigDecimal extraPersonFee;

    private Long totalRooms;
    private List<RoomImgResponse> images;

    private List<AssetResponse> assets;

    private Double averageRating;
}
