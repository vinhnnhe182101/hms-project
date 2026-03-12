package com.product.hms.dto.response;

import com.product.hms.enums.RoomStatus;
import java.io.Serializable;

/**
 * DTO phản hồi thông tin phòng vật lý (Room).
 * @param id           ID phòng
 * @param roomNumber   Số phòng
 * @param roomClassId  ID loại phòng
 * @param roomClassName Tên loại phòng
 * @param status       Trạng thái phòng
 * @param isActive     Trạng thái hoạt động
 */
public record RoomResponse(
    Long id,
    String roomNumber,
    Long roomClassId,
    String roomClassName,
    RoomStatus status,
    Boolean isActive
) implements Serializable {}

