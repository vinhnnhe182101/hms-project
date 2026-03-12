package com.product.hms.dto.request;

import com.product.hms.enums.RoomChangeType;
import jakarta.validation.constraints.NotNull;

/**
 * Request chuyển phòng cho khách đã check-in.
 * 
 * @param newRoomId ID phòng mới muốn chuyển sang
 * @param changeType Loại chuyển phòng: CUSTOMER_REQUEST (khách yêu cầu), ROOM_ISSUE (do sự cố phòng)
 * @param note Ghi chú lý do chuyển phòng (bắt buộc nếu là ROOM_ISSUE)
 */
public record RoomChangeRequest(
    @NotNull Long newRoomId,
    @NotNull RoomChangeType changeType,
    String note
) {}
