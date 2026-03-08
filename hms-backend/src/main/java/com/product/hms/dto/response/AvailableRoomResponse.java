package com.product.hms.dto.response;

/**
 * Response DTO for a single available physical room.
 *
 * @param roomId     room id
 * @param roomNumber room number
 */
public record AvailableRoomResponse(
        Long roomId,
        String roomNumber
) {
}

