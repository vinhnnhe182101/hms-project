package com.product.hms.dto.request;

import java.sql.Timestamp;
import java.util.List;

/**
 * DTO cho thao tác đặt phòng (tạo mới hoặc cập nhật)
 *
 * @param customerRequest     Thông tin khách hàng (có thể là khách cũ qua customerId hoặc khách mới)
 * @param roomClassQuantities Danh sách loại phòng và số lượng (cho phép lặp loại phòng với số người khác nhau)
 * @param checkInDate         Ngày nhận phòng dự kiến
 * @param checkOutDate        Ngày trả phòng dự kiến
 * @param numberOfMembers     Tổng số khách
 * @param note                Ghi chú hoặc yêu cầu đặc biệt
 */
public record ReservationRequest(
        CustomerRequest customerRequest,
        List<RoomClassQuantityRequest> roomClassQuantities,
        Timestamp checkInDate,
        Timestamp checkOutDate,
        Integer numberOfMembers,
        String note
) {
}

