package com.product.hms.dto.response;

/**
 * Response DTO for room class availability information
 *
 * @param roomClass      Room class details
 * @param availableRooms Number of available rooms for this class
 */
public record RoomClassAvailabilityResponse(
        RoomClassResponse roomClass,
        Integer availableRooms
) {
}



