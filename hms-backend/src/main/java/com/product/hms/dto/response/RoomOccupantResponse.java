package com.product.hms.dto.response;

/**
 * DTO phản hồi thông tin người ở trong phòng.
 *
 * @param customer Thông tin khách hàng
 * @param role     Vai trò trong phòng (ví dụ: "PRIMARY", "COMPANION")
 */
public record RoomOccupantResponse(
        CustomerResponse customer,
        String role
) {
}

