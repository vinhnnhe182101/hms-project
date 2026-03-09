package com.product.hms.dto.request;

/**
 * Room assignment payload for one reservation-room during check-in.
 *
 * @param reservationRoomId reservation room id
 * @param roomId            physical room id to assign
 */
public record ReservationRoomCheckInRequest(
        Long reservationRoomId,
        Long roomId
) {
}

