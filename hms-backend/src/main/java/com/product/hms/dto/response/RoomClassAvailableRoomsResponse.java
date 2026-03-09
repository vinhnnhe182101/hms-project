package com.product.hms.dto.response;

import java.util.List;

/**
 * Response DTO for available physical rooms grouped by room class.
 *
 * @param roomClass      room class details
 * @param availableRooms available physical rooms for this class
 */
public record RoomClassAvailableRoomsResponse(
        RoomClassResponse roomClass,
        List<AvailableRoomResponse> availableRooms
) {
}

