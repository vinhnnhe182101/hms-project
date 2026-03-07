package com.product.hms.dto.request;

/**
 * Request DTO for customer information
 *
 * @param customerId   Existing customer ID (optional)
 * @param identityCard Customer identity card number
 * @param fullName     Customer full name
 * @param phoneNumber  Customer phone number
 * @param email        Customer email address
 */
public record CustomerRequest(
        Long customerId,
        String identityCard,
        String fullName,
        String phoneNumber,
        String email
) {
}
