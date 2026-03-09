package com.product.hms.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO phản hồi chi tiết hóa đơn phòng cho preview trả phòng.
 *
 * @param reservationRoomId ID allocation phòng
 * @param roomNumber        Số phòng vật lý
 * @param roomClassName     Tên loại phòng
 * @param occupants         Danh sách người ở trong phòng
 * @param folioItems        Danh sách khoản mục hóa đơn (các loại phí)
 * @param totalCharges      Tổng tiền phí
 * @param totalPaid         Tổng tiền đã thanh toán
 * @param balance           Số dư còn lại phải thanh toán
 */
public record ReservationRoomFolioResponse(
        Long reservationRoomId,
        String roomNumber,
        String roomClassName,
        List<RoomOccupantResponse> occupants,
        List<FolioItemResponse> folioItems,
        BigDecimal totalCharges,
        BigDecimal totalPaid,
        BigDecimal balance
) {
}
