package com.product.hms.dto.request;

/**
 * DTO yêu cầu tạo hoặc cập nhật đặt dịch vụ
 */
public record ServiceBookingRequest(
        Long serviceId,
        Integer quantity,
        String notes
) {
}

