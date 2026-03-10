package com.product.hms.dto.response;

/**
 * DTO phản hồi thông tin số lượng phòng còn trống theo loại phòng.
 *
 * @param roomClass      Thông tin loại phòng
 * @param availableRooms Số lượng phòng còn trống của loại này
 */
public record RoomClassAvailabilityResponse(
        RoomClassResponse roomClass,
        Integer availableRooms
) {
}
