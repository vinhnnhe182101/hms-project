package com.product.hms.dto.request;

import com.product.hms.enums.RoomStatus;

/**
 * DTO filter cho API search phòng (Room).
 * <ul>
 *   <li>roomNumber: Tìm kiếm theo số phòng (LIKE, không phân biệt hoa thường).</li>
 *   <li>roomClassId: Lọc theo hạng phòng.</li>
 *   <li>status: Lọc theo trạng thái phòng.</li>
 * </ul>
 */
public record RoomSearchFilter(
    String roomNumber,
    Long roomClassId,
    RoomStatus status
) {}
