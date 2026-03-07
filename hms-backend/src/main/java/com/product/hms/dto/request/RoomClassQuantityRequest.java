package com.product.hms.dto.request;

/**
 * Request DTO for room class allocation information
 * <p>
 * Cho phép người dùng chỉ định rõ ràng:
 * - Họ đặt hạng phòng nào (roomClassId)
 * - Bao nhiêu người sẽ ở trong allocation này (numberOfPeople)
 * <p>
 * Ví dụ:
 * - RoomClassQuantityRequest(1L, 2) → Đặt hạng phòng Standard cho 2 người
 * - RoomClassQuantityRequest(1L, 3) → Đặt hạng phòng Standard cho 3 người (allocation riêng)
 *
 * @param roomClassId    ID of the room class
 * @param numberOfPeople Number of people for this allocation
 */
public record RoomClassQuantityRequest(
        Long roomClassId,
        Integer numberOfPeople
) {
}

