package com.product.hms.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * DTO phản hồi thông tin đặt dịch vụ.
 *
 * @param id                ID đặt dịch vụ
 * @param reservationRoomId ID allocation phòng liên quan
 * @param serviceId         ID dịch vụ
 * @param serviceName       Tên dịch vụ
 * @param quantity          Số lượng dịch vụ
 * @param unitPrice         Đơn giá dịch vụ
 * @param totalAmount       Thành tiền
 * @param status            Trạng thái đặt dịch vụ
 * @param notes             Ghi chú
 * @param createdAt         Thời điểm tạo
 */
public record ServiceBookingResponse(
        Long id,
        Long reservationRoomId,
        Long serviceId,
        String serviceName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        String status,
        String notes,
        Timestamp createdAt
) {
}

