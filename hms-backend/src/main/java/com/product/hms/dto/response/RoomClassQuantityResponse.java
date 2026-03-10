package com.product.hms.dto.response;

/**
 * DTO phản hồi thông tin phân bổ loại phòng.
 * <p>
 * Chứa chi tiết allocation bao gồm cả ID để tracking.
 * <p>
 * Ví dụ:
 * - RoomClassQuantityResponse(101L, 1L, 2) → Allocation ID 101, phòng Standard, 2 người
 *
 * @param id             ID allocation (reservation_room_allocation ID)
 * @param roomClassId    ID loại phòng
 * @param numberOfPeople Số người cho allocation này
 */
public record RoomClassQuantityResponse(
        Long id,
        Long roomClassId,
        Integer numberOfPeople
) {
}
