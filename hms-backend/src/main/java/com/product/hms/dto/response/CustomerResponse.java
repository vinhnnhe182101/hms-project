package com.product.hms.dto.response;

/**
 * Response DTO for customer information
 *
 * @param id           Customer ID
 * @param fullName     Customer full name
 * @param phoneNumber  Customer phone number
 * @param identityCard Customer identity card number
 * @param email        Customer email address
 * @param type         Customer type (REGULAR, VIP, etc.)
 */
public record CustomerResponse(
        Long id,
        String fullName,
        String phoneNumber,
        String identityCard,
        String email,
        String type
) {
}



