package com.product.hms.dto.request;

/**
 * DTO yêu cầu thông tin phân bổ loại phòng
 * <p>
 * Cho phép người dùng chỉ định rõ ràng:
 * - Họ đặt hạng phòng nào (roomClassId)
 * - Bao nhiêu người sẽ ở trong allocation này (numberOfPeople)
 * <p>
 * Ví dụ:
 * - RoomClassQuantityRequest(1L, 2) → Đặt hạng phòng Standard cho 2 người
 * - RoomClassQuantityRequest(1L, 3) → Đặt hạng phòng Standard cho 3 người (allocation riêng)
 *
 * @param roomClassId    ID loại phòng
 * @param numberOfPeople Số người cho allocation này
 */
public record RoomClassQuantityRequest(
        Long roomClassId,
        Integer numberOfPeople
) {
}

