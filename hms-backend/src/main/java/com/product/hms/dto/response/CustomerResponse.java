package com.product.hms.dto.response;

/**
 * DTO phản hồi thông tin khách hàng.
 *
 * @param id           ID khách hàng
 * @param fullName     Họ tên khách hàng
 * @param phoneNumber  Số điện thoại
 * @param identityCard Số CMND/CCCD
 * @param email        Địa chỉ email
 * @param type         Loại khách hàng (REGULAR, VIP, ...)
 */
public record CustomerResponse(
        Long id,
        String fullName,
        String phoneNumber,
        String identityCard,
        String email,
        String type
) {
}
