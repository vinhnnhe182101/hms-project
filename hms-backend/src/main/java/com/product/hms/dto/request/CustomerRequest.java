package com.product.hms.dto.request;

/**
 * DTO yêu cầu cho thông tin khách hàng
 *
 * @param customerId   ID khách hàng đã tồn tại (tùy chọn)
 * @param identityCard Số CMND/CCCD của khách hàng
 * @param fullName     Họ tên khách hàng
 * @param phoneNumber  Số điện thoại khách hàng
 * @param email        Địa chỉ email khách hàng
 */
public record CustomerRequest(
        Long customerId,
        String identityCard,
        String fullName,
        String phoneNumber,
        String email
) {
}
