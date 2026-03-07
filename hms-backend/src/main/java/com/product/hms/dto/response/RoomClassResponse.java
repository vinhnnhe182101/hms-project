package com.product.hms.dto.response;

import java.math.BigDecimal;

/**
 * Response DTO for room class information
 *
 * @param id               Room class ID
 * @param name             Room class name
 * @param basePrice        Base price per night
 * @param standardCapacity Standard number of guests
 * @param maxCapacity      Maximum number of guests allowed
 * @param extraPersonFee   Additional fee per extra person
 */
public record RoomClassResponse(
        Long id,
        String name,
        BigDecimal basePrice,
        Integer standardCapacity,
        Integer maxCapacity,
        BigDecimal extraPersonFee
) {
}



