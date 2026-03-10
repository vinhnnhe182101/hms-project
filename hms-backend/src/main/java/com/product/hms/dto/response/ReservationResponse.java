package com.product.hms.dto.response;

import java.sql.Timestamp;
import java.util.List;

/**
 * DTO phản hồi thông tin đơn đặt phòng.
 *
 * @param bookingId       ID đơn đặt phòng
 * @param bookingCode     Mã đặt phòng duy nhất
 * @param customer        Thông tin khách hàng đầy đủ
 * @param allocations     Danh sách phân bổ loại phòng (không gộp, mỗi entry là một allocation riêng)
 * @param checkInDate     Ngày nhận phòng dự kiến
 * @param checkOutDate    Ngày trả phòng dự kiến
 * @param status          Trạng thái đơn đặt phòng (CONFIRMED, CHECKED_IN, ...)
 * @param numberOfMembers Tổng số khách
 * @param note            Ghi chú cho đơn đặt phòng
 * @param createdAt       Thời điểm tạo đơn
 */
public record ReservationResponse(
        Long bookingId,
        String bookingCode,
        CustomerResponse customer,
        List<RoomClassQuantityResponse> allocations,
        Timestamp checkInDate,
        Timestamp checkOutDate,
        String status,
        Integer numberOfMembers,
        String note,
        Timestamp createdAt
) {
}
