package com.product.hms.dto.response;

/**
 * DTO phản hồi sau khi trả phòng.
 *
 * @param reservationRoomId ID allocation phòng đã trả
 * @param status            Trạng thái mới (CHECKED_OUT)
 * @param message           Thông báo thành công
 */
public record ReservationRoomCheckOutResponse(
        Long reservationRoomId,
        String status,
        String message
) {
}

