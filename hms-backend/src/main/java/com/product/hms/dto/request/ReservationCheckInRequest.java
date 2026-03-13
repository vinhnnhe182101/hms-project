package com.product.hms.dto.request;

import java.util.List;

/**
 * DTO yêu cầu check-in cho đơn đặt phòng.
 *
 * @param autoAssign      Nếu true, hệ thống tự động gán phòng cho các allocation chưa có trong roomAssignments
 * @param roomAssignments Danh sách gán phòng thủ công (có thể chỉ định một phần nếu autoAssign=true)
 */
public record ReservationCheckInRequest(
        Boolean autoAssign,
        List<ReservationRoomCheckInRequest> roomAssignments
) {
}

