package com.product.hms.dto.response;

import java.util.List;

/**
 * DTO phản hồi danh sách phòng vật lý còn trống theo từng loại phòng.
 *
 * @param roomClass      Thông tin loại phòng
 * @param availableRooms Danh sách phòng vật lý còn trống của loại này
 */
public record RoomClassAvailableRoomsResponse(
        RoomClassResponse roomClass,
        List<AvailableRoomResponse> availableRooms
) {
}

