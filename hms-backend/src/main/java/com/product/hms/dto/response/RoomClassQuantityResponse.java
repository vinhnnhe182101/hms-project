package com.product.hms.dto.response;

/**
 * Response DTO for room class allocation information
 * <p>
 * Contains allocation details including ID for tracking
 * <p>
 * Ví dụ:
 * - RoomClassQuantityResponse(101L, 1L, 2) → Allocation ID 101, Standard room, 2 người
 *
 * @param id             Allocation ID (reservation_room_allocation ID)
 * @param roomClassId    ID of the room class
 * @param numberOfPeople Number of people for this allocation
 */
public record RoomClassQuantityResponse(
        Long id,
        Long roomClassId,
        Integer numberOfPeople
) {
}

