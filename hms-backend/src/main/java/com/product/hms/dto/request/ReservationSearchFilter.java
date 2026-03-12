package com.product.hms.dto.request;

import com.product.hms.enums.ReservationStatus;
import java.time.LocalDate;

/**
 * DTO filter cho API search đặt phòng (Reservation).
 * <ul>
 *   <li>guestName: Tìm kiếm theo tên khách (LIKE, không phân biệt hoa thường).</li>
 *   <li>status: Lọc theo trạng thái đặt phòng.</li>
 *   <li>checkInDateFrom, checkInDateTo: Lọc theo khoảng ngày check-in.</li>
 * </ul>
 */
public record ReservationSearchFilter(
    String guestName,
    ReservationStatus status,
    LocalDate checkInDateFrom,
    LocalDate checkInDateTo
) {}
