package com.product.hms.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AdminRoomTypeResponse(
        Long id,
        String typeName,
        Integer standardOccupancy,
        Integer maxOccupancy,
        BigDecimal baseRatePerNight
) {
}
