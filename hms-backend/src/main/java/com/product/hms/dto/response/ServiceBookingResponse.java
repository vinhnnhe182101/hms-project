package com.product.hms.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Response DTO for service booking
 */
public record ServiceBookingResponse(
        Long id,
        Long reservationRoomId,
        Long serviceId,
        String serviceName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        String status,
        String notes,
        Timestamp createdAt
) {
}

