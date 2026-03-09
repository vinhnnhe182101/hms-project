package com.product.hms.dto.response;

import java.math.BigDecimal;

/**
 * DTO phản hồi thông tin loại phòng.
 *
 * @param id               ID loại phòng
 * @param name             Tên loại phòng
 * @param basePrice        Giá cơ bản mỗi đêm
 * @param standardCapacity Sức chứa tiêu chuẩn
 * @param maxCapacity      Sức chứa tối đa
 * @param extraPersonFee   Phụ phí mỗi người vượt chuẩn
 */
public record RoomClassResponse(
        Long id,
        String name,
        BigDecimal basePrice,
        Integer standardCapacity,
        Integer maxCapacity,
        BigDecimal extraPersonFee
) {
}
