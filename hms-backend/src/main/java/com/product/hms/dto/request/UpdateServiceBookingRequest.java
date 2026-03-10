package com.product.hms.dto.request;

/**
 * DTO yêu cầu cập nhật đặt dịch vụ (chỉ cho phép thay đổi số lượng)
 *
 * @param quantity Số lượng dịch vụ mới
 * @param notes    Ghi chú bổ sung (nếu có)
 */
public record UpdateServiceBookingRequest(
        Integer quantity,
        String notes
) {
}
