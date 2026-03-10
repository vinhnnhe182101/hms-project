package com.product.hms.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * DTO phản hồi giao dịch thanh toán.
 *
 * @param paymentId          ID giao dịch thanh toán
 * @param paymentCode        Mã giao dịch
 * @param paymentMethod      Phương thức thanh toán
 * @param selectedItemsTotal Tổng tiền các khoản mục được chọn
 * @param depositApplied     Số tiền đặt cọc áp dụng cho thanh toán này
 * @param cashCollected      Số tiền mặt/thẻ thực thu
 * @param status             Trạng thái thanh toán
 * @param remainingBalance   Số dư hóa đơn còn lại sau thanh toán
 * @param paymentUrl         URL chuyển hướng VNPAY (null nếu không phải VNPAY)
 * @param createdAt          Thời điểm thanh toán
 */
public record PaymentResponse(
        Long paymentId,
        String paymentCode,
        String paymentMethod,
        BigDecimal selectedItemsTotal,
        BigDecimal depositApplied,
        BigDecimal cashCollected,
        String status,
        BigDecimal remainingBalance,
        String paymentUrl,
        Timestamp createdAt
) {
}
