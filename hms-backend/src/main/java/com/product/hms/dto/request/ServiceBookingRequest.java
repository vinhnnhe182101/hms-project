package com.product.hms.dto.request;

/**
 * Request DTO for creating or updating service booking
 */
public record ServiceBookingRequest(
        Long serviceId,
        Integer quantity,
        String notes
) {
}

