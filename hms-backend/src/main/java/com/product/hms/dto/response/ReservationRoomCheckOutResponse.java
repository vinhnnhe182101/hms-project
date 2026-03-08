package com.product.hms.dto.response;

/**
 * Response DTO after checking out a room
 *
 * @param reservationRoomId Reservation room ID that was checked out
 * @param status            New status (should be CHECKED_OUT)
 * @param message           Success message
 */
public record ReservationRoomCheckOutResponse(
        Long reservationRoomId,
        String status,
        String message
) {
}

