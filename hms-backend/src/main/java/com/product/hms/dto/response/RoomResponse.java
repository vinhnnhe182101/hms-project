package com.product.hms.dto.response;

import com.product.hms.enums.RoomStatus;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO phản hồi thông tin phòng vật lý (Room) bao gồm thông tin lưu trú hiện tại.
 *
 * @param id               ID phòng
 * @param roomNumber       Số phòng
 * @param roomClassId      ID loại phòng
 * @param roomClassName    Tên loại phòng
 * @param status           Trạng thái phòng (Ví dụ: OCCUPIED)
 * @param isActive         Trạng thái hoạt động của phòng
 * @param bookingCode      Mã đặt phòng hiện tại (nếu có)
 * @param guestFullName    Tên khách đang ở (nếu có)
 * @param guestPhoneNumber Số điện thoại khách (nếu có)
 * @param checkInDate      Ngày check-in thực tế/dự kiến
 * @param checkOutDate     Ngày check-out thực tế/dự kiến
 */
public record RoomResponse(
        Long id,
        String roomNumber,
        Long roomClassId,
        String roomClassName,
        RoomStatus status,
        Boolean isActive,

        // Thông tin Booking được gộp trực tiếp
        String bookingCode,
        String guestFullName,
        String guestPhoneNumber,
        LocalDate checkInDate,
        LocalDate checkOutDate
) implements Serializable {
}