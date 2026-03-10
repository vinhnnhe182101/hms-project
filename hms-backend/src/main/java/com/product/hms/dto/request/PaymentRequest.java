package com.product.hms.dto.request;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO yêu cầu xử lý thanh toán cho phòng đặt.
 *
 * @param folioItemIds  Danh sách ID khoản mục hóa đơn được chọn để thanh toán
 * @param paymentMethod Phương thức thanh toán (CASH, CARD, BANK_TRANSFER, QR, VNPAY)
 * @param depositAmount Số tiền đặt cọc sẽ trừ vào thanh toán này (tùy chọn)
 * @param clientIp      Địa chỉ IP client để sinh URL VNPAY (bắt buộc nếu paymentMethod = VNPAY)
 * @param returnUrl     URL trả về sau khi thanh toán VNPAY (bắt buộc nếu paymentMethod = VNPAY)
 * @param note          Ghi chú thanh toán (tùy chọn)
 */
public record PaymentRequest(
        List<Long> folioItemIds,
        String paymentMethod,
        BigDecimal depositAmount,
        String clientIp,
        String returnUrl,
        String note
) {
}
