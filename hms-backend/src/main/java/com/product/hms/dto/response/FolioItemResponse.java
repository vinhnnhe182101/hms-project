package com.product.hms.dto.response;

import java.math.BigDecimal;

/**
 * DTO phản hồi chi tiết khoản mục hóa đơn.
 *
 * @param id          ID khoản mục
 * @param type        Loại khoản mục (ROOM_CHARGE, SERVICE_CHARGE, ...)
 * @param description Mô tả khoản mục
 * @param quantity    Số lượng
 * @param totalPrice  Tổng tiền
 * @param status      Trạng thái thanh toán (PAID, UNPAID, VOID)
 */
public record FolioItemResponse(
        Long id,
        String type,
        String description,
        Integer quantity,
        BigDecimal totalPrice,
        String status
) {
}
