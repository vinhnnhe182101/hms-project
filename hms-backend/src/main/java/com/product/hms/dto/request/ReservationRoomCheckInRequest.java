package com.product.hms.dto.request;

/**
 * DTO gán phòng cho từng allocation khi check-in.
 *
 * @param reservationRoomId ID allocation đặt phòng
 * @param roomId            ID phòng thực tế sẽ gán
 */
public record ReservationRoomCheckInRequest(
        Long reservationRoomId,
        Long roomId
) {
}

