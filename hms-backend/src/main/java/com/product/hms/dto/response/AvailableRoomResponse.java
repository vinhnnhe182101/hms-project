package com.product.hms.dto.response;

/**
 * DTO phản hồi cho một phòng vật lý còn trống.
 *
 * @param roomId     ID phòng
 * @param roomNumber Số phòng
 */
public record AvailableRoomResponse(
        Long roomId,
        String roomNumber
) {
}
