package com.product.hms.dto.response;

/**
 * Response DTO for room occupant information
 *
 * @param customer Customer information
 * @param role     Role in the room (e.g., "PRIMARY", "COMPANION")
 */
public record RoomOccupantResponse(
        CustomerResponse customer,
        String role
) {
}

