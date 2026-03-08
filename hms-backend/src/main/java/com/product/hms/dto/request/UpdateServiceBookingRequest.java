package com.product.hms.dto.request;

/**
 * Request DTO for updating service booking (only quantity can be changed)
 */
public record UpdateServiceBookingRequest(
        Integer quantity,
        String notes
) {
}

